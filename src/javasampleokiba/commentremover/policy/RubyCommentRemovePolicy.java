package javasampleokiba.commentremover.policy;

/**
 * Ruby�p�̃R�����g�폜�|���V�[�N���X.
 */
public class RubyCommentRemovePolicy extends DefaultCommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"#"};
    }

    @Override
    public String[] getMultiLineCommentBegin() {
        return new String[]{"=begin"};
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return new String[]{"=end"};
    }

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\"", "'"};
    }
}