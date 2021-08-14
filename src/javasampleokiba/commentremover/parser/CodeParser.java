package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javasampleokiba.commentremover.CommentRemover.Mode;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * �\�[�X�R�[�h����͂�����N���X.
 */
public class CodeParser {

    /** ��͑Ώۂ̃e�L�X�g�f�[�^ */
    protected final List<String> lines_;
    /** �R�����g�폜�|���V�[ */
    protected final CommentRemovePolicy policy_;
    private final CodePosition begin_;
    private CodePosition end_;

    /**
     * �I�u�W�F�N�g���\�z���܂��B
     * 
     * @param lines   �e�L�X�g�f�[�^
     * @param begin   �J�n�ʒu���
     * @param policy  �R�����g�폜�|���V�[
     */
    public CodeParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy) {
        lines_ = lines;
        begin_ = begin;
        policy_ = policy;
    }

    /**
     * �J�n�ʒu�����擾���܂��B
     * 
     * @return �J�n�ʒu���
     */
    public CodePosition getBegin() {
        return begin_;
    }

    /**
     * �I���ʒu�����擾���܂��B
     * 
     * @return �I���ʒu���
     */
    public CodePosition getEnd() {
        return end_;
    }

    /**
     * �I���ʒu����ݒ肵�܂��B
     * 
     * @param end  �I���ʒu���
     */
    protected void setEnd(CodePosition end) {
        end_ = end;
    }

    /**
     * �w�肳�ꂽ�s�ʒu�ɂ����邱�̃I�u�W�F�N�g�ł̉�́E�������ʂ̕�������擾���܂��B
     * 
     * @param row   �s�ʒu
     * @param mode  �������[�h
     * @return ��́E�������ʂ̕�����
     * @see Mode
     */
    public SimpleEntry<String, Boolean> getString(int row, Mode mode) {
        if (mode == Mode.REMOVE) {
            return new SimpleEntry<>(getString(row), false);
        } else {
            return new SimpleEntry<>("", true);
        }
    }

    /**
     * �w�肳�ꂽ�s�ʒu�ɂ����鏈���O�̃I���W�i���̕�������擾���܂��B
     * 
     * @param row  �s�ʒu
     * @return �����O�̃I���W�i���̕�����
     */
    public String getString(int row) {
        String line = lines_.get(row);
        CodePosition begin = getBegin();
        CodePosition end = getEnd();

        if (row == begin.row() && row == end.row()) {
            return line.substring(begin.pos(), end.pos());
        } else if (row == begin.row()) {
            return line.substring(begin.pos());
        } else if (row == end.row()) {
            return line.substring(0, end.pos());
        } else {
            return line;
        }
    }

    /**
     * ���̃I�u�W�F�N�g�̉�́E�����͈͂̂��ׂĂ̕�������擾���܂��B
     * 
     * @return ��́E�����͈͂̂��ׂĂ̕�����
     */
    public List<String> getAllString() {
        List<String> result = new ArrayList<String>();
        CodePosition begin = getBegin();
        CodePosition end = getEnd();
        for (int row = begin.row(); row <= end.row(); row++) {
            result.add(getString(row));
        }
        return result;
    }

    /**
     * �w�肳�ꂽ�s�ʒu�����̃I�u�W�F�N�g�̉�́E�����͈͓������肵�܂��B
     * 
     * @param row  �s�ʒu
     * @return ��́E�����͈͓��̏ꍇ�� {@code true}
     */
    public boolean withinRow(int row) {
        return begin_.row() <= row && row <= end_.row();
    }

    /**
     * ���̃I�u�W�F�N�g�̉�́E�����͈͂���(���������s�������Ȃ�)�ł��邩���肵�܂��B
     * 
     * @return ��́E�����͈͂���ł���ꍇ�� {@code true}
     */
    public boolean isEmpty() {
        return begin_.row() == end_.row() &&
                begin_.pos() == end_.pos();
    }

    /**
     * ���̃I�u�W�F�N�g�̉�́E�����͈͂̎��͈̔͂�S������I�u�W�F�N�g�𐶐����ĕԂ��܂��B
     * 
     * @return ���͈̔͂�S������\�[�X�R�[�h��̓I�u�W�F�N�g
     * @throws ParseException  ��̓G���[�����������ꍇ
     */
    public CodeParser next() throws ParseException {
        String[] comments = policy_.getCommentString();
        String[] beginComments = policy_.getMultiLineCommentBegin();
        String[] endComments = policy_.getMultiLineCommentEnd();
        String[] quotationMarks = policy_.getQuotationMarks();
        int beginPos = begin_.pos();

        // ���̃R�����g�J�n������A�܂��͈��p�������񂪌�����܂Ń��[�v
        for (int row = begin_.row(); row < lines_.size(); row++) {
            String line = lines_.get(row);
            SimpleEntry<Integer, String> e;

            // ���p��������ʒu�擾
            e = getIndexWithoutEscaped(line, beginPos, quotationMarks);
            int qmPos = e.getKey();
            String quotationMark = e.getValue();

            // �R�����g������ʒu�擾
            e = getIndex(line, beginPos, comments);
            int cPos = e.getKey();
            String comment = e.getValue();

            // �R�����g�J�n�{�I��������ʒu�擾
            e = getIndex(line, beginPos, beginComments, endComments);
            int ccPos = e.getKey();
            String combinedComment = e.getValue();

            // �R�����g�J�n������ʒu�擾
            e = getIndex(line, beginPos, beginComments);
            int bcPos = e.getKey();
            String beginComment = e.getValue();

            // ���p�������񂪃R�����g�E�R�����g�J�n����������O�ɂ���H
            if (isAhead(qmPos, quotationMark, cPos, comment) &&
                isAhead(qmPos, quotationMark, bcPos, beginComment)) {
                end_ = new CodePosition(row, qmPos);
                return new LiteralParser(lines_, new CodePosition(row, qmPos), policy_,
                        quotationMark);
            }

            // �R�����g�����񂪃R�����g�J�n����������O�ɂ���H
            if (isAhead(cPos, comment, bcPos, beginComment)) {
                end_ = new CodePosition(row, cPos);
                return new CommentParser(lines_, new CodePosition(row, cPos), policy_,
                        comment);
            }

            // �R�����g�J�n�{�I�������񂪃R�����g�J�n����������O�ɂ���H
            if (isAhead(ccPos, bcPos)) {
                end_ = new CodePosition(row, ccPos);
                return new MutliLineCommentParser(lines_, new CodePosition(row, ccPos), policy_,
                        combinedComment);
            }

            // �R�����g�J�n�����񂪂���H
            if (0 <= bcPos) {
                end_ = new CodePosition(row, bcPos);
                return new MutliLineCommentParser(lines_, new CodePosition(row, bcPos), policy_,
                        beginComment);
            }

            // �R�����g�E�R�����g�J�n�����񂪂Ȃ��Ȃ玟�̍s��
            beginPos = 0;
        }

        // �S�s��͏I��
        if (lines_.isEmpty()) {
            end_ = new CodePosition(0, 0);
        } else {
            int lastRow = lines_.size() - 1;
            end_ = new CodePosition(lastRow, lines_.get(lastRow).length());
        }
        return null;
    }

    @Override
    public String toString() {
        return "CodeParser [begin=" + begin_ + ", end=" + end_ + "]";
    }

    /**
     * ����CodeParser�I�u�W�F�N�g�𐶐����ĕԂ��܂��B
     * 
     * @param row  �s�ʒu
     * @param pos  �����ʒu
     * @return �V����CodeParser�I�u�W�F�N�g
     */
    protected CodeParser createNextCodeParser(int row, int pos) {
        if (pos < lines_.get(row).length()) {
            return new CodeParser(lines_, new CodePosition(row, pos), policy_);
        } else {
            if (++row < lines_.size()) {
                return new CodeParser(lines_, new CodePosition(row, 0), policy_);
            }
        }
        return null;
    }

    /**
     * �w�肳�ꂽ1�ȏ�̌���������̂����A�ł��擪�ɋ߂�������̕����ʒu�A������̃y�A���擾���܂��B
     * ������Ȃ��ꍇ�́A-1, {@code null}�̃y�A��Ԃ��܂��B
     * 
     * @param str      �����Ώە�����
     * @param start    �����J�n�ʒu
     * @param targets  ����������
     * @return �����ʒu�A������̃y�A
     */
    protected SimpleEntry<Integer, String> getIndex(String str, int start, String... targets) {
        if (targets == null) {
            return new SimpleEntry<>(-1, null);
        }

        int minIdx = -1;
        String found = null;
        for (String target : targets) {
            if (isValid(target)) {
                int idx = str.indexOf(target, start);
                if (0 <= idx && (idx < minIdx || minIdx == -1)) {
                    minIdx = idx;
                    found = target;
                }
            }
        }
        return new SimpleEntry<>(minIdx, found);
    }

    /**
     * �w�肳�ꂽ1�ȏ�̌���������̂����A�ł��擪�ɋ߂�������̕����ʒu�A������̃y�A���擾���܂��B
     * ����������́A�R�����g�J�n������ƃR�����g�I�����������������������ł��B
     * ������Ȃ��ꍇ�́A-1, {@code null}�̃y�A��Ԃ��܂��B
     * 
     * @param str      �����Ώە�����
     * @param start    �����J�n�ʒu
     * @param begins   �R�����g�J�n������
     * @param ends     �R�����g�I��������
     * @return �����ʒu�A������̃y�A
     */
    protected SimpleEntry<Integer, String> getIndex(String str, int start, String[] begins, String[] ends) {
        if (begins == null || ends == null) {
            return new SimpleEntry<>(-1, null);
        }

        int minIdx = -1;
        String found = null;
        for (int i = 0; i < begins.length; i++) {
            String target = begins[i] + ends[i];
            if (isValid(target)) {
                int idx = str.indexOf(target, start);
                if (0 <= idx && (idx < minIdx || minIdx == -1)) {
                    minIdx = idx;
                    found = begins[i];
                }
            }
        }
        return new SimpleEntry<>(minIdx, found);
    }

    /**
     * �w�肳�ꂽ1�ȏ�̌���������̂����A�ł��擪�ɋ߂�������̕����ʒu�A������̃y�A���擾���܂��B
     * �����Ώە����񂪌��������ꍇ�ł�"\"�ɂ���ăG�X�P�[�v����Ă���ꍇ�͂���𖳎����܂��B
     * 
     * @param str      �����Ώە�����
     * @param start    �����J�n�ʒu
     * @param targets  ����������
     * @return �����ʒu�A������̃y�A
     */
    protected SimpleEntry<Integer, String> getIndexWithoutEscaped(String str, int start,
            String... targets) {
        if (targets == null) {
            return new SimpleEntry<>(-1, null);
        }

        int minIdx = -1;
        String found = null;
        for (String target : targets) {
            if (isValid(target)) {
                int idx = indexWithoutEscapeSeq(str, start, target);
                if (0 <= idx && (idx < minIdx || minIdx == -1)) {
                    minIdx = idx;
                    found = target;
                }
            }
        }
        return new SimpleEntry<>(minIdx, found);
    }

    private int indexWithoutEscapeSeq(String str, int start, String target) {
        int pos = str.indexOf(target, start);
        if (pos >= 0) {
            int count = 0;
            // ����������̑O�̃G�X�P�[�v�����̐����J�E���g
            for (int i = pos - 1; i >= start; i--) {
                if (str.charAt(i) == '\\') {
                    count++;
                } else {
                    break;
                }
            }
            // �����Ȃ猟�������񂪃G�X�P�[�v����Ă���킯�ł͂Ȃ��̂ŁA���̈ʒu��ԋp
            if (count % 2 == 0) {
                return pos;
            } else {
                // ���������񂪃G�X�P�[�v����Ă���̂ŁA����Ɍ㑱������
                return indexWithoutEscapeSeq(str, pos + 1, target);
            }
        }
        return -1;
    }

    private boolean isAhead(int idx1, String str1, int idx2, String str2) {
        if (idx1 < 0) {
            return false;
        }
        if (idx2 == -1 || idx1 < idx2) {
            return true;
        } else if (idx1 == idx2 && str2.length() < str1.length()) {
            return true;
        }
        return false;
    }

    private boolean isAhead(int idx1, int idx2) {
        if (idx1 < 0) {
            return false;
        }
        return idx2 == -1 || idx1 <= idx2;
    }

    private boolean isValid(String str) {
        return !(str == null || str.equals(""));
    }
}