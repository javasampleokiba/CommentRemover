package javasampleokiba.commentremover.policy;

/**
 * JavaScript�p�̃R�����g�폜�|���V�[�N���X.
 */
public class JavaScriptCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\"", "'"};
    }
}