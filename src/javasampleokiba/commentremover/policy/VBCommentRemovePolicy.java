package javasampleokiba.commentremover.policy;

/**
 * VB, VB.NET, VBS, VBA�p�̃R�����g�폜�|���V�[�N���X.
 */
public class VBCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"'"};
    }
}