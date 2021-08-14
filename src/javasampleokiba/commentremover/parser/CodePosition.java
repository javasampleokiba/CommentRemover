package javasampleokiba.commentremover.parser;

/**
 * �\�[�X�R�[�h��̈ʒu���(�s�ʒu�A�����ʒu)��ێ�����N���X.
 */
public class CodePosition {

    private final int row_;
    private final int pos_;

    /**
     * �w�肳�ꂽ�ʒu�������I�u�W�F�N�g���\�z���܂��B
     * 
     * @param row  �s�ʒu(0�I���W��)
     * @param pos  �����ʒu(0�I���W��)
     */
    public CodePosition(int row, int pos) {
        row_ = row;
        pos_ = pos;
    }

    /**
     * �s�ʒu(0�I���W��)���擾���܂��B
     * 
     * @return �s�ʒu
     */
    public int row() {
        return row_;
    }

    /**
     * �����ʒu(0�I���W��)���擾���܂��B
     * 
     * @return �����ʒu
     */
    public int pos() {
        return pos_;
    }

    @Override
    public String toString() {
        return "CodePosition [row=" + row_ + ", pos=" + pos_ + "]";
    }
}