package javasampleokiba.commentremover.policy;

/**
 * JavaScript用のコメント削除ポリシークラス.
 */
public class JavaScriptCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\"", "'"};
    }
}
