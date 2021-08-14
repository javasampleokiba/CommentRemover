package javasampleokiba.commentremover.policy;

/**
 * PHP�p�̃R�����g�폜�|���V�[�N���X.
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