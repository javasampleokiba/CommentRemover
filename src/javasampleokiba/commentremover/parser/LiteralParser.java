package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * ���e�������������͂���N���X.
 */
public class LiteralParser extends CodeParser {

    private final String quotationMark_;

    /**
     * �I�u�W�F�N�g���\�z���܂��B
     * 
     * @param lines          �e�L�X�g�f�[�^
     * @param begin          �J�n�ʒu���
     * @param policy         �R�����g�폜�|���V�[
     * @param quotationMark  ���p��������
     */
    public LiteralParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy,
            String quotationMark) {
        super(lines, begin, policy);
        quotationMark_ = quotationMark;
    }

    @Override
    public CodeParser next() throws ParseException {
        int beginPos = getBegin().pos() + quotationMark_.length();

        // (�I����)���p�������񂪌�����܂Ń��[�v
        for (int row = getBegin().row(); row < lines_.size(); row++) {
            String line = lines_.get(row);
            SimpleEntry<Integer, String> e = getIndexWithoutEscaped(line, beginPos, quotationMark_);
            int qmPos = e.getKey();

            // ���p�������񂪂���H
            if (0 <= qmPos) {
                int pos = qmPos + quotationMark_.length();
                setEnd(new CodePosition(row, pos));
                return createNextCodeParser(row, pos);
            }

            // ���p�������񂪂Ȃ��Ȃ玟�̍s��
            beginPos = 0;
        }

        // �S�s��͏I��
        throw new ParseException("�I���̈��p����������܂���B", getBegin().row());
    }

    @Override
    public String toString() {
        return "LiteralParser [quotationMark=" + quotationMark_ + ", begin=" + getBegin() + ", end=" + getEnd() + "]";
    }
}