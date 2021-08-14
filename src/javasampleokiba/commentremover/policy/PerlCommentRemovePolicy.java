package javasampleokiba.commentremover.policy;

/**
 * Perl用のコメント削除ポリシークラス.
 */
public class PerlCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"#"};
    }

    @Override
    public String[] getMultiLineCommentBegin() {
        return new String[]{"=pod"};
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return new String[]{"=cut"};
    }

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\"", "'"};
    }
}
