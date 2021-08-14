package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * 複数行コメント文字列を解析するクラス.
 */
public class MutliLineCommentParser extends CommentParser {

    /**
     * オブジェクトを構築します。
     * 
     * @param lines    テキストデータ
     * @param begin    開始位置情報
     * @param policy   コメント削除ポリシー
     * @param comment  コメント開始文字列
     */
    public MutliLineCommentParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy,
            String comment) {
        super(lines, begin, policy, comment);
    }

    @Override
    public CodeParser next() throws ParseException {
        String endComment = getEndComment();
        int beginPos = getBegin().pos() + getComment().length();

        // コメント終了文字列が見つかるまでループ
        for (int row = getBegin().row(); row < lines_.size(); row++) {
            String line = lines_.get(row);
            SimpleEntry<Integer, String> e = getIndex(line, beginPos, endComment);
            int ecPos = e.getKey();

            // コメント終了文字列がある？
            if (0 <= ecPos) {
                int pos = ecPos + endComment.length();
                setEnd(new CodePosition(row, pos));
                return createNextCodeParser(row, pos);
            }

            // コメント終了文字列がないなら次の行へ
            beginPos = 0;
        }

        // 全行解析終了
        throw new ParseException("コメント終了文字列が見つかりません。", getBegin().row());
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
