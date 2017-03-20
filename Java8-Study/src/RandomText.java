import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomText {

	/**
	 * 引数を3つ取るPredicate
	 */
	@FunctionalInterface
	public static interface IntTernaryPredicate{
		boolean test(int a, int b, int c);
	}

	public static String randomText(int words, int lower, int upper, List<String> delimiters){

		IntTernaryPredicate withinRange = (val, l, u) -> {
			return l <= val && val <= u;
		};

		//0x20はスペース、21～2Fは記号
//		IntPredicate symbol = i -> withinRange.is(i, 0x20, 0x2f);
		//alphabetと数字 0x5cはバックスラッシュなので除く
		IntPredicate alnum = i -> withinRange.test(i, 0x30, 0x7e)  && i != 0x5c;
		//ひらがな
		IntPredicate hiragana = i -> withinRange.test(i, 0x3041, 0x3094);
		//カタカナ
		IntPredicate katakana = i -> withinRange.test(i, 0x30a1, 0x30f4);
		//半角カナ
		IntPredicate hkana = i -> withinRange.test(i, 0xff66, 0xff9f);

		//連結して1つのIntPredicateを得る
		IntPredicate acceptLetter = alnum.or(hiragana).or(katakana).or(hkana);

		//使用する文字のリストを得る
		List<String> chars = IntStream.rangeClosed(0x1, 0xff9f)
				.filter(acceptLetter)
				//Unicodeコードポイント(int)→UTF-16文字(char[])→String
				//String.valueOf(i)としてしまうと数値がそのまま文字になるだけなので✕
			    .mapToObj(i -> String.valueOf(Character.toChars(i)))
			    .collect(Collectors.toList());

		//区切り文字をランダムに取得する
		Supplier<String> delm;
		if(delimiters.size() == 1){
			//要素が一つの場合はそれを返すだけのラムダ式に
			String d = delimiters.get(0);
			delm = () -> d;
		}else{
			//複数あったら乱数で
			delm = () -> delimiters.get(
					ThreadLocalRandom.current().nextInt(delimiters.size()));
		}

		//引数の文字数でランダムな文字列を生成するラムダ式
		IntFunction<String> getWord = i ->
			//リストのインデックスの範囲で乱数生成
			ThreadLocalRandom.current().ints(0, chars.size())
				//渡された長さまで
				.limit(i)
				//インデックスから文字を取り出す
				.mapToObj(chars::get)
				//１つの文字列へ結合
				.collect(Collectors.joining());

		//文字数下限～上限の範囲で乱数生成
		String result = ThreadLocalRandom.current().ints(lower, upper)
				.parallel()
				//渡された単語数分まで
				.limit(words)
				//ランダムな単語を得る
				.mapToObj(getWord)
				//結合 Collectors#joining(String)だと区切り文字が１つしか指定できないので3つ指定する方
				//supplierはStringBuilderのコンストラクタ
				.collect(StringBuilder::new,
						//accumulatorは区切り文字を追加して結合
						(sb, str) -> sb.append(delm.get()).append(str),
						//combinerはStringBuilderの場合appendでコンテナ同士を結合できる
						StringBuilder::append)
				//上記だと一番先頭にも区切り文字が入ってしまうため、toStringではなくsubstring(1)で除去しつつStringへ
				.substring(1);

		return result;

	}

	public static void main(String[] args) {

//		List<String> delimiters = Arrays.asList(" ","\r","\n","\t");
		List<String> delimiters = Arrays.asList(" ");

		String result = randomText(10_000, 3, 8, delimiters);
		System.out.println(result);
	}


}
