import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomText {

	@FunctionalInterface
	public static interface IntTernaryPredicate{
		boolean is(int a, int b, int c);
	}
	public static void main(String[] args) throws InterruptedException {

//		List<String> delimiters = Arrays.asList(" ","\r","\n","\t");
		List<String> delimiters = Arrays.asList(" ");

		String result = randomText(10_000, 3, 8, delimiters);
		System.out.println(result);
	}

	public static String randomText(int words, int lower, int upper, List<String> delimiters){

		IntTernaryPredicate withinRange = (val, l, u) -> {
			return l <= val && val <= u;
		};

		//0x20はスペース、21～2Fは記号
		IntPredicate symbol = i -> withinRange.is(i, 0x20, 0x2f);
		//alphabetと数字 0x5cはバックスラッシュなので除く
		IntPredicate alnum = i -> withinRange.is(i, 0x30, 0x7e)  && i != 0x5c;
		//ひらがな
		IntPredicate hiragana = i -> withinRange.is(i, 0x3041, 0x3094);
		//カタカナ
		IntPredicate katakana = i -> withinRange.is(i, 0x30a1, 0x30f4);
		//半角カナ
		IntPredicate hkana = i -> withinRange.is(i, 0xff66, 0xff9f);

		IntPredicate acceptLetter = alnum.or(hiragana).or(katakana).or(hkana);

		List<String> chars = IntStream.rangeClosed(0x1, 0xff9f)
				.filter(acceptLetter)
			    .mapToObj(i -> String.valueOf(Character.toChars(i)))
			    .collect(Collectors.toList());

		Supplier<String> delm;
		if(delimiters.size() == 1){
			String d = delimiters.get(0);
			delm = () -> d;
		}else{
			delm = () -> delimiters.get(ThreadLocalRandom.current().nextInt(delimiters.size()));
		}

		String result = ThreadLocalRandom.current().ints(words, lower, upper)
			.parallel()
			.mapToObj(len ->
				ThreadLocalRandom.current().ints(0, chars.size())
					.limit(len)
					.mapToObj(chars::get)
					.collect(Collectors.joining())
			)
			.collect(() -> new StringBuilder(words * upper / 10),
					(sb, str) -> sb.append(delm.get()).append(str),
					StringBuilder::append)
			.substring(1);

		return result;

	}

	public static boolean withinRange(int val, int lower, int upper){
		return lower <= val && val <= upper;
	}
}
