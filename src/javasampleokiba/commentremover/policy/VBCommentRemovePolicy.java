package javasampleokiba.commentremover.policy;

/**
 * VB, VB.NET, VBS, VBA用のコメント削除ポリシークラス.
 */
public class VBCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"'"};
    }
}
