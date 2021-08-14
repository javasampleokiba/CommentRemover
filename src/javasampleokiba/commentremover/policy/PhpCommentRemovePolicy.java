package javasampleokiba.commentremover.policy;

/**
 * PHP用のコメント削除ポリシークラス.
 */
public class PhpCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"//", "#"};
    }

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\"", "'"};
    }
}
