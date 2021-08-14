package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.regex.Pattern;

import javasampleokiba.commentremover.CommentRemover.Mode;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * �P��s�R�����g���������͂���N���X.
 */
public class CommentParser extends CodeParser {

    private final String comment_;
    private Boolean canRemove_      = null;

    /**
     * �I�u�W�F�N�g���\�z���܂��B
     * 
     * @param lines    �e�L�X�g�f�[�^
     * @param begin    �J�n�ʒu���
     * @param policy   �R�����g�폜�|���V�[
     * @param comment  �R�����g�J�n������
     */
    public CommentParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy,
            String comment) {
        super(lines, begin, policy);
        comment_ = comment;
    }

    /**
     * �R�����g�J�n��������擾���܂��B
     * 
     * @return �R�����g�J�n������
     */
    public String getComment() {
        return comment_;
    }

    @Override
    public SimpleEntry<String, Boolean> getString(int row, Mode mode) {
        if (mode == Mode.REMOVE && willRemove()) {
            return new SimpleEntry<>("", true);
        }
        return new SimpleEntry<>(getString(row), false);
    }

    @Override
    public CodeParser next() throws ParseException {
        int row = getBegin().row();
        String line = lines_.get(row);
        setEnd(new CodePosition(row, line.length()));

        if (++row < lines_.size()) {
            return new CodeParser(lines_, new CodePosition(row, 0), policy_);
        }
        return null;
    }

    @Override
    public String toString() {
        return "CommentParser [begin=" + getBegin() + ", end=" + getEnd() + "]";
    }

    /**
     * ���̃I�u�W�F�N�g�̉�́E�����͈͂̃R�����g�J�n����������������ׂĂ̕�������擾���܂��B
     * 
     * @return ��́E�����͈͂̂��ׂĂ̕�����
     */
    protected List<String> getAllStringWithoutCommentString() {
        List<String> lines = getAllString();
        lines.set(0, lines.get(0).substring(comment_.length()));
        return lines;
    }

    /**
     * ���̃I�u�W�F�N�g�̃R�����g���폜�Ώۂ��ǂ������肵�܂��B
     * 
     * @return �폜�Ώۂ̏ꍇ�� {@code true}
     */
    protected boolean willRemove() {
        // ��x���肵�Ă���ꍇ�͂��̌��ʂ�Ԃ�
        if (canRemove_ != null) {
            return canRemove_;
        }

        List<String> lines = getAllStringWithoutCommentString();

        // �폜�Ώۃ|���V�[�m�F
        Pattern rp = policy_.getRemovePattern();
        if (rp != null) {
            canRemove_ = lines.stream().anyMatch(e -> rp.matcher(e).find());
            return canRemove_;
        }

        // �폜�ΏۊO�|���V�[�m�F
        Pattern ep = policy_.getExceptPattern();
        if (ep != null) {
            canRemove_ = !lines.stream().anyMatch(e -> ep.matcher(e).find());
            return canRemove_;
        }
        canRemove_ = true;
        return canRemove_;
    }
}