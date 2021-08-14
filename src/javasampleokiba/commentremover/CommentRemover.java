package javasampleokiba.commentremover;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javasampleokiba.commentremover.parser.CodeParser;
import javasampleokiba.commentremover.parser.CodePosition;
import javasampleokiba.commentremover.policy.CustomCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * ソースコメントの一括削除および抽出を行うクラス.
 *
 * <p>[解析アルゴリズム]</p>
 * <ul>
 * <li>コメントの開始を表す文字列（以下、コメント開始文字列）が行の中で検出された場合に、
 * その文字列より後ろの部分（複数行コメントの場合は次の行以降も）をコメントとみなします。</li>
 * <li>1行に複数のコメント開始文字列が存在する場合は、最も先頭のものをコメント開始文字列と解釈します。</li>
 * <li>コメント開始文字列が複数定義されている場合は、最長マッチが優先です。例えば、/**は/*よりも優先して検出されます。</li>
 * <li>コメントの開始または終了を表す文字列（以下、コメント文字列）は大文字小文字が区別されます。</li>
 * <li>引用符で囲まれたリテラル文字列中に現れるコメント文字列は、コメント文字列とみなさず無視されます。</li>
 * <li>リテラル文字列中のバックスラッシュによる引用符のエスケープに対応しています。</li>
 * <li>言語毎に文法的に正しいかについては検知しません。例えば、コメント開始文字列は行頭になくてはならないという言語仕様があったとしても、
 * 行の途中で検出されればそれはコメント開始文字列とみなします。</li>
 * <li>テキストの終了までコメント終了文字列、あるいは閉じの引用符が見つからない場合は、実行時エラーとなります。</li>
 * </ul>
 *
 * <p>[制限事項]</p>
 * <ul>
 * <li>ヒアドキュメントには対応していないため、ヒアドキュメント中でもコメント文字列が検出されます。</li>
 * <li>言語毎の引用符以外による特殊なリテラル文字列には対応していないため、それらリテラル文字列中でもコメント文字列が検出されます。</li>
 * <li>コメント開始文字列に続く文字列が何であろうとコメントとみなすため、例えばPHPの場合、
 * コメント行の途中にコード終了タグ?>がある場合でも行末までコメントとみなされます。</li>
 * <li>上記の別の制限例として、例えばVBのremをコメント開始文字列として定義する場合、
 * "rem "などのように空白文字を付与する必要があります（そうでないとremxxxのような文字列もコメントであるとみなされるため）。</li>
 * </ul>
 */
public class CommentRemover {

    /** 処理モード */
    public enum Mode {
        /** コメント削除 */
        REMOVE,
        /** コメント抽出 */
        EXTRACT,
    }

    private CommentRemover() {
    }

    /**
     * 指定されたテキストデータから、指定されたポリシーに従って、コメントを削除します。
     * 
     * @param lines   テキストデータ
     * @param policy  コメント削除ポリシー
     * @return コメント削除後のテキストデータ
     * @throws ParseException  解析エラーが発生した場合
     */
    public static List<String> remove(List<String> lines, CommentRemovePolicy policy)
            throws ParseException {
        return execute(lines, policy, Mode.REMOVE);
    }

    /**
     * 指定されたテキストデータから、指定されたポリシーに従って、コメントを抽出します。
     * つまり、{@link #remove(List, CommentRemovePolicy)}とは逆に、コメント以外をすべて削除します。
     * 
     * @param lines   テキストデータ
     * @param policy  コメント削除ポリシー
     * @return コメント抽出後のテキストデータ
     * @throws ParseException  解析エラーが発生した場合
     */
    public static List<String> extract(List<String> lines, CommentRemovePolicy policy)
            throws ParseException {
        return execute(lines, policy, Mode.EXTRACT);
    }

    /**
     * 指定されたテキストデータから、指定されたポリシーに従って、コメントを削除または抽出します。
     * 
     * @param lines   テキストデータ
     * @param policy  コメント削除ポリシー
     * @param mode    処理モード
     * @return コメント削除または抽出後のテキストデータ
     * @see Mode
     * @throws IllegalArgumentException  ポリシーの設定が不正な場合
     * @throws ParseException  解析エラーが発生した場合
     */
    public static List<String> execute(List<String> lines, CommentRemovePolicy policy, Mode mode)
            throws ParseException {
        // コメント削除ポリシーをチェック
        checkPolicy(policy);
        // コメント削除ポリシーを正常化
        CommentRemovePolicy normalizedPolicy = normalizedPolicy(policy);

        List<CodeParser> allParsers = new ArrayList<CodeParser>();
        List<String> result = new ArrayList<String>();

        // 解析
        CodeParser parser = new CodeParser(lines, new CodePosition(0, 0), normalizedPolicy);
        do {
            allParsers.add(parser);
            parser = parser.next();
        } while(parser != null);

        // 解析結果からコメント削除 or 抽出
        for (int i = 0; i < lines.size(); i++) {
            int row = i;
            List<CodeParser> parsers = allParsers.stream()
                    .filter(e -> e.withinRow(row)).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            boolean removed = false;

            for (CodeParser cp : parsers) {
                SimpleEntry<String, Boolean> e = cp.getString(row, mode);
                sb.append(e.getKey());
                removed |= e.getValue();
            }

            // 追加対象のデータであれば結果に格納
            String line = sb.toString();
            if (willAdd(line, removed, normalizedPolicy)) {
                result.add(line);
            }
        }

        return result;
    }

    private static void checkPolicy(CommentRemovePolicy policy) {
        String[] beginComments = policy.getMultiLineCommentBegin();
        String[] endComments = policy.getMultiLineCommentEnd();

        if (beginComments != null ^ endComments != null) {
            throw new IllegalArgumentException("コメント開始文字列とコメント終了文字列の片方だけを有効にすることはできません。");

        } else if (beginComments != null && endComments != null) {
            if (beginComments.length != endComments.length) {
                throw new IllegalArgumentException("コメント開始文字列とコメント終了文字列の数が異なります。");
            }
            for (int i = 0; i < beginComments.length; i++) {
                if (isValid(beginComments[i]) ^ isValid(endComments[i])) {
                    throw new IllegalArgumentException("コメント開始文字列とコメント終了文字列の片方だけを有効にすることはできません。");
                }
            }
        }
    }

    private static boolean isValid(String str) {
        return !(str == null || str.equals(""));
    }

    private static CommentRemovePolicy normalizedPolicy(CommentRemovePolicy policy) {
        CommentRemovePolicy result = new CustomCommentRemovePolicy(policy);
        String[] comments = result.getCommentString();
        String[] beginComments = result.getMultiLineCommentBegin();
        String[] endComments = result.getMultiLineCommentEnd();
        boolean found;

        /*
         * コメント文字列、コメント開始文字列が複数定義されている場合、順番を整える。
         * 例えば、/*、/**の場合は/**が先に検索されるようにするため、/*よりも前に移動。
         */
        if (comments != null) {
            do {
                found = false;
                for (int i = 0; i < comments.length - 1; i++) {
                    for (int j = i + 1; j < comments.length; j++) {
                        if (comments[i].length() < comments[j].length() &&
                            comments[j].startsWith(comments[i])) {
                            swap(comments, i, j);
                            found = true;
                            break;
                        }
                    }
                }
            } while (found);
        }

        if (beginComments != null) {
            do {
                found = false;
                for (int i = 0; i < beginComments.length - 1; i++) {
                    for (int j = i + 1; j < beginComments.length; j++) {
                        if (beginComments[i].length() < beginComments[j].length() &&
                            beginComments[j].startsWith(beginComments[i])) {
                            swap(beginComments, i, j);
                            swap(endComments, i, j);
                            found = true;
                            break;
                        }
                    }
                }
            } while (found);
        }

        return result;
    }

    private static void swap(String[] arr, int idx1, int idx2) {
        String tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;
    }

    private static boolean willAdd(String line, boolean removed, CommentRemovePolicy policy) {
        // 削除を行っていない場合
        if (!removed) {
            return true;
        }

        // 空行を削除する設定、かつ空行の場合
        if (policy.isEnabledRemoveEmptyLine() &&
                line.equals("")) {
            return false;
        }

        // 空白行を削除する設定、かつ空白行の場合
        if (policy.isEnabledRemoveBlankLine() &&
                policy.getBlankPattern() != null &&
                policy.getBlankPattern().matcher(line).matches()) {
            return false;
        }
        return true;
    }
}
