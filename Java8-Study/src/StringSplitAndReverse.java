import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StringSplitAndReverse {

	public static void main(String[] args) {

		String s = "あおいうふぇ abc.lxu,fewag'ow$fowajfw#fg-あfaoi~oijgr|freagj!foajg |afewag,/$aaa";

		/*
		 * 文字列を区切り文字も含んで分割する
		 */
		//StringTokenizerで分割
//		splitTokenizer(s, "|").stream()
//			.forEach(System.out::println);

		//正規表現の肯定先読み・肯定後読みで分割
//		splitRegExp(s, " \\.,'#,!/\\|\\$~-").stream()
//			.forEach(System.out::println);

		//正規表現の単語境界で分割
		List<String> result = splitRegExp2(s);

		/*
		 * リストを逆順にする
		 */
		//Collections#reverseを使う
//		Collections.reverse(result);

		//Stream#sortedを使う
		result.stream()
			//Comparatorとして常に-1を返すと逆順に
			.sorted((a,b) -> -1)
			.forEach(System.out::println);
	}

	/**
	 * StringTokenizerを使った文字列分割。
	 * @param str 分割対象の文字列
	 * @param delim 区切り文字
	 * @return
	 */
	public static List<String> splitTokenizer(String str, String delim){
		List<String> list = new ArrayList<>();

		//3つ目の引数にtrueを渡すと、区切り文字もトークンとして取得できる
		//・・・が、区切り文字は1つしか指定できない
		StringTokenizer st = new StringTokenizer(str, delim, true);

		//Iterableを実装してないので拡張for文は使えず・・・
		while(st.hasMoreTokens()){
			list.add(st.nextToken());
		}
		return list;
	}

	/**
	 * 正規表現先読み/後読みを使った文字列分割。
	 * https://abicky.net/2010/05/30/135112/
	 * @param str
	 * @param delim
	 * @return
	 */
	public static List<String> splitRegExp(String str, String delim){

		//(?<=X)肯定後読み・・・直前にXがある位置にマッチする
		//(?=X)肯定先読み・・・直後にXがある位置にマッチする
		String[] strs = str.split("((?<=[" + delim + "])|(?=[" + delim + "]))");

		//文字クラスを使えば複数の区切り文字が使える
		//・・・が連続した場合それぞれ1文字で分割してしまう
		return Arrays.asList(strs);
	}

	/**
	 * 境界正規表現エンジンで分割。
	 * @param str
	 * @return
	 */
	public static List<String> splitRegExp2(String str){

		//\b→単語境界にマッチする
		String[] strs = str.split("\\b");
		return Arrays.asList(strs);
	}
}
