package javasampleokiba.commentremover.policy;

import java.util.regex.Pattern;

/**
 * コメント削除ポリシーインターフェース.
 */
public interface CommentRemovePolicy {

    /**
     * 単一行コメントの開始文字列を取得します（複数指定可）。
     * {@code null}または空配列を返すと、単一行コメント削除が無効になります。
     * 
     * @return コメント開始文字列
     */
    String[] getCommentString();

    /**
     * 複数行コメントの開始文字列を取得します（複数指定可）。
     * {@code null}または空配列を返すと、複数行コメント削除が無効になります。
     * 
     * @return 複数行コメント開始文字列
     */
    String[] getMultiLineCommentBegin();

    /**
     * 複数行コメントの終了文字列を取得します（複数指定可）。
     * {@code null}または空配列を返すと、複数行コメント削除が無効になります。
     * ※複数指定する場合は、{@link #getMultiLineCommentBegin()}と順番を合わせてください。
     * 
     * @return 複数行コメント終了文字列
     */
    String[] getMultiLineCommentEnd();

    /**
     * コメント削除によって空行となった行を削除するか判定します。
     * 
     * @return 空行を削除する場合は {@code true}
     */
    boolean isEnabledRemoveEmptyLine();

    /**
     * コメント削除によって空白行となった行を削除するか判定します。
     * {@link #getBlankPattern()}で返された文字列を空白文字とみなします。
     * 
     * @return 空白行を削除する場合は {@code true}
     */
    boolean isEnabledRemoveBlankLine();

    /**
     * 空白文字とみなす文字列（正規表現）を取得します。
     * {@code null}を返すと空白文字指定なしになります。
     * 
     * @return 空白文字（正規表現）
     */
    Pattern getBlankPattern();

    /**
     * 引用符文字列を取得します（複数指定可）。
     * {@code null}を返すと引用符文字列指定なしになります。
     * 
     * @return 引用符文字列
     */
    String[] getQuotationMarks();

    /**
     * コメント削除時の削除対象のコメント（正規表現）を取得します。
     * {@code null}を返すと指定なしになり、すべてのコメントが削除対象になります。
     * 
     * @return 削除対象のコメント（正規表現）
     */
    Pattern getRemovePattern();

    /**
     * コメント削除時の削除対象外のコメント（正規表現）を取得します。
     * {@code null}を返すと指定なしになり、すべてのコメントが削除対象になります。
     * {@link #getRemovePattern()}も指定されている場合はそちらが優先されます。
     * 
     * @return 削除対象外のコメント（正規表現）
     */
    Pattern getExceptPattern();
}
