package javasampleokiba.commentremover.policy;

/**
 * Java用のコメント削除ポリシークラス.
 */
public class JavaCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getMultiLineCommentBegin() {
        return new String[]{"/**", "/*"};
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return new String[]{"*/", "*/"};
    }
}
