package javasampleokiba.commentremover.policy;

import java.util.regex.Pattern;

/**
 * カスタマイズ用のコメント削除ポリシークラス.
 */
public class CustomCommentRemovePolicy implements CommentRemovePolicy {

    private String[] commentString_         = null;
    private String[] multiLineCommentBegin_ = null;
    private String[] multiLineCommentEnd_   = null;
    private boolean enabledRemoveEmptyLine_ = false;
    private boolean enabledRemoveBlankLine_ = false;
    private Pattern blankPattern_           = null;
    private String[] quotationMarks_        = null;
    private Pattern removePattern_          = null;
    private Pattern exceptPattern_          = null;

    /**
     * すべての設定がクリアされたオブジェクトを構築します。
     */
    public CustomCommentRemovePolicy() {
    }

    /**
     * 指定されたポリシーの設定を引き継いだオブジェクトを構築します。
     * 
     * @param policy  設定を引き継ぐコメント削除ポリシー
     */
    public CustomCommentRemovePolicy(CommentRemovePolicy policy) {
        commentString_ = policy.getCommentString();
        multiLineCommentBegin_ = policy.getMultiLineCommentBegin();
        multiLineCommentEnd_ = policy.getMultiLineCommentEnd();
        enabledRemoveEmptyLine_ = policy.isEnabledRemoveEmptyLine();
        enabledRemoveBlankLine_ = policy.isEnabledRemoveBlankLine();
        blankPattern_ = policy.getBlankPattern();
        quotationMarks_ = policy.getQuotationMarks();
        removePattern_ = policy.getRemovePattern();
        exceptPattern_ = policy.getExceptPattern();
    }

    @Override
    public String[] getCommentString() {
        return commentString_;
    }

    /**
     * 単一行コメントの開始文字列を設定します（複数指定可）。
     * {@code null}または空配列を設定すると、単一行コメント削除が無効になります。
     * 
     * @param commentString  コメント開始文字列
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setCommentString(String[] commentString) {
        commentString_ = commentString;
        return this;
    }

    @Override
    public String[] getMultiLineCommentBegin() {
        return multiLineCommentBegin_;
    }

    /**
     * 複数行コメントの開始文字列を設定します（複数指定可）。
     * {@code null}または空配列を設定すると、複数行コメント削除が無効になります。
     * 
     * @param multiLineCommentBegin  複数行コメント開始文字列
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setMultiLineCommentBegin(String[] multiLineCommentBegin) {
        multiLineCommentBegin_ = multiLineCommentBegin;
        return this;
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return multiLineCommentEnd_;
    }

    /**
     * 複数行コメントの終了文字列を設定します（複数指定可）。
     * {@code null}または空配列を設定すると、複数行コメント削除が無効になります。
     * ※複数指定する場合は、{@link #setMultiLineCommentBegin(String[])}と順番を合わせてください。
     * 
     * @param multiLineCommentEnd  複数行コメント終了文字列
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setMultiLineCommentEnd(String[] multiLineCommentEnd) {
        multiLineCommentEnd_ = multiLineCommentEnd;
        return this;
    }

    @Override
    public boolean isEnabledRemoveEmptyLine() {
        return enabledRemoveEmptyLine_;
    }

    /**
     * コメント削除によって空行となった行を削除するか設定します。
     * 
     * @param enabledRemoveEmptyLine  空行を削除する場合は {@code true}
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setEnabledRemoveEmptyLine(boolean enabledRemoveEmptyLine) {
        enabledRemoveEmptyLine_ = enabledRemoveEmptyLine;
        return this;
    }

    @Override
    public boolean isEnabledRemoveBlankLine() {
        return enabledRemoveBlankLine_;
    }

    /**
     * コメント削除によって空白行となった行を削除するか設定します。
     * {@link #setBlankPattern(Pattern)}で設定された文字列を空白文字とみなします。
     * 
     * @param enabledRemoveBlankLine  空白行を削除する場合は {@code true}
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setEnabledRemoveBlankLine(boolean enabledRemoveBlankLine) {
        enabledRemoveBlankLine_ = enabledRemoveBlankLine;
        return this;
    }

    @Override
    public Pattern getBlankPattern() {
        return blankPattern_;
    }

    /**
     * 空白文字とみなす文字列（正規表現）を設定します。
     * {@code null}を設定すると空白文字指定なしになります。
     * 
     * @param blankPattern  空白文字（正規表現）
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setBlankPattern(Pattern blankPattern) {
        blankPattern_ = blankPattern;
        return this;
    }

    @Override
    public String[] getQuotationMarks() {
        return quotationMarks_;
    }

    /**
     * 引用符文字列を設定します（複数指定可）。
     * {@code null}を設定すると引用符文字列指定なしになります。
     * 
     * @param quotationMarks  引用符文字列
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setQuotationMarks(String[] quotationMarks) {
        quotationMarks_ = quotationMarks;
        return this;
    }

    @Override
    public Pattern getRemovePattern() {
        return removePattern_;
    }

    /**
     * コメント削除時の削除対象のコメント（正規表現）を設定します。
     * {@code null}を設定すると指定なしになり、すべてのコメントが削除対象になります。
     * 
     * @param removePattern  削除対象のコメント（正規表現）
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setRemovePattern(Pattern removePattern) {
        removePattern_ = removePattern;
        return this;
    }

    @Override
    public Pattern getExceptPattern() {
        return exceptPattern_;
    }

    /**
     * コメント削除時の削除対象外のコメント（正規表現）を設定します。
     * {@code null}を設定すると指定なしになり、すべてのコメントが削除対象になります。
     * {@link #setRemovePattern(Pattern)}も指定されている場合はそちらが優先されます。
     * 
     * @param exceptPattern  削除対象外のコメント（正規表現）
     * @return このオブジェクトの参照
     */
    public CustomCommentRemovePolicy setExceptPattern(Pattern exceptPattern) {
        exceptPattern_ = exceptPattern;
        return this;
    }
}
