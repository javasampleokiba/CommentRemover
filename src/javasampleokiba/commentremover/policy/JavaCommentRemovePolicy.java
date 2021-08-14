package javasampleokiba.commentremover.policy;

/**
 * Java�p�̃R�����g�폜�|���V�[�N���X.
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