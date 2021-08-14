package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * リテラル文字列を解析するクラス.
 */
public class LiteralParser extends CodeParser {

    private final String quotationMark_;

    /**
     * オブジェクトを構築します。
     * 
     * @param lines          テキストデータ
     * @param begin          開始位置情報
     * @param policy         コメント削除ポリシー
     * @param quotationMark  引用符文字列
     */
    public LiteralParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy,
            String quotationMark) {
        super(lines, begin, policy);
        quotationMark_ = quotationMark;
    }

    @Override
    public CodeParser next() throws ParseException {
        int beginPos = getBegin().pos() + quotationMark_.length();

        // (終了の)引用符文字列が見つかるまでループ
        for (int row = getBegin().row(); row < lines_.size(); row++) {
            String line = lines_.get(row);
            SimpleEntry<Integer, String> e = getIndexWithoutEscaped(line, beginPos, quotationMark_);
            int qmPos = e.getKey();

            // 引用符文字列がある？
            if (0 <= qmPos) {
                int pos = qmPos + quotationMark_.length();
                setEnd(new CodePosition(row, pos));
                return createNextCodeParser(row, pos);
            }

            // 引用符文字列がないなら次の行へ
            beginPos = 0;
        }

        // 全行解析終了
        throw new ParseException("終了の引用符が見つかりません。", getBegin().row());
    }

    @Override
    public String toString() {
        return "LiteralParser [quotationMark=" + quotationMark_ + ", begin=" + getBegin() + ", end=" + getEnd() + "]";
    }
}
