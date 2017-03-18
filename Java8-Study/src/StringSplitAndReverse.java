import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringSplitAndReverse {

	public static void main(String[] args) {

		String s = "あおいうふぇ abc.lxu,fewag'ow$fowajfw#fg-あfaoi~oijgr|freagj!foajg |afewag,/$aaa";

//		splitTokenizer(s, "|").stream()
//			.forEach(System.out::println);

//		splitRegExp(s, " \\.,'#,!/\\|\\$~-").stream()
//			.forEach(System.out::println);

		splitRegExp2(s).stream()
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
		StringTokenizer st = new StringTokenizer(str, delim, true);

		//Iterableを実装してないので拡張for文は使えず・・・
		while(st.hasMoreTokens()){
			list.add(st.nextToken());
		}
		return list;
	}

	/**
	 * 正規表現先読み/後読みを使った文字列分割。
	 * @param str
	 * @param delim
	 * @return
	 */
	public static List<String> splitRegExp(String str, String delim){
		//https://abicky.net/2010/05/30/135112/
		//(?=X)肯定後読み
		//(?=X)肯定先読み
		Pattern p = Pattern.compile("((?<=[" + delim + "])|(?=[" + delim + "]))");
		String[] strs = p.split(str);

		return Arrays.asList(strs);
	}

	public static List<String> splitRegExp2(String str){

		String[] strs = str.split("\\b");
		return Arrays.asList(strs);
	}
}
