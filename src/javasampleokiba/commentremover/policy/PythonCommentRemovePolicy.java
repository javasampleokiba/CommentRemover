package javasampleokiba.commentremover.policy;

/**
 * Python�p�̃R�����g�폜�|���V�[�N���X.
 */
public class PythonCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"#"};
    }

    @Override
    public String[] getMultiLineCommentBegin() {
        return null;
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return null;
    }

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\"", "'"};
    }
}