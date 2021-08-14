package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * �����s�R�����g���������͂���N���X.
 */
public class MutliLineCommentParser extends CommentParser {

    /**
     * �I�u�W�F�N�g���\�z���܂��B
     * 
     * @param lines    �e�L�X�g�f�[�^
     * @param begin    �J�n�ʒu���
     * @param policy   �R�����g�폜�|���V�[
     * @param comment  �R�����g�J�n������
     */
    public MutliLineCommentParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy,
            String comment) {
        super(lines, begin, policy, comment);
    }

    @Override
    public CodeParser next() throws ParseException {
        String endComment = getEndComment();
        int beginPos = getBegin().pos() + getComment().length();

        // �R�����g�I�������񂪌�����܂Ń��[�v
        for (int row = getBegin().row(); row < lines_.size(); row++) {
            String line = lines_.get(row);
            SimpleEntry<Integer, String> e = getIndex(line, beginPos, endComment);
            int ecPos = e.getKey();

            // �R�����g�I�������񂪂���H
            if (0 <= ecPos) {
                int pos = ecPos + endComment.length();
                setEnd(new CodePosition(row, pos));
                return createNextCodeParser(row, pos);
            }

            // �R�����g�I�������񂪂Ȃ��Ȃ玟�̍s��
            beginPos = 0;
        }

        // �S�s��͏I��
        throw new ParseException("�R�����g�I�������񂪌�����܂���B", getBegin().row());
    }

    @Override
    public String toString() {
        return "MutliLineCommentParser [begin=" + getBegin() + ", end=" + getEnd() + "]";
    }

    @Override
    protected List<String> getAllStringWithoutCommentString() {
        String endComment = getEndComment();
        List<String> lines = getAllString();

        if (lines.size() == 1) {
            String line = lines.get(0);
            lines.set(0, line.substring(getComment().length(), line.length() - endComment.length()));

        } else {
            String line = lines.get(0);
            lines.set(0, line.substring(getComment().length()));

            line = lines.get(lines.size() - 1);
            lines.set(lines.size() - 1, line.substring(0, line.length() - endComment.length()));
        }
        return lines;
    }

    private String getEndComment() {
        String[] beginComments = policy_.getMultiLineCommentBegin();
        for (int i = 0; i < beginComments.length; i++) {
            if (getComment().equals(beginComments[i])) {
                return policy_.getMultiLineCommentEnd()[i];
            }
        }
        return null;
    }
}