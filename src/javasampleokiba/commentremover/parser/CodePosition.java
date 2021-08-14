package javasampleokiba.commentremover.parser;

/**
 * ソースコード上の位置情報(行位置、文字位置)を保持するクラス.
 */
public class CodePosition {

    private final int row_;
    private final int pos_;

    /**
     * 指定された位置情報を持つオブジェクトを構築します。
     * 
     * @param row  行位置(0オリジン)
     * @param pos  文字位置(0オリジン)
     */
    public CodePosition(int row, int pos) {
        row_ = row;
        pos_ = pos;
    }

    /**
     * 行位置(0オリジン)を取得します。
     * 
     * @return 行位置
     */
    public int row() {
        return row_;
    }

    /**
     * 文字位置(0オリジン)を取得します。
     * 
     * @return 文字位置
     */
    public int pos() {
        return pos_;
    }

    @Override
    public String toString() {
        return "CodePosition [row=" + row_ + ", pos=" + pos_ + "]";
    }
}
