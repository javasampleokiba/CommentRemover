package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.regex.Pattern;

import javasampleokiba.commentremover.CommentRemover.Mode;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * 単一行コメント文字列を解析するクラス.
 */
public class CommentParser extends CodeParser {

    private final String comment_;
    private Boolean canRemove_      = null;

    /**
     * オブジェクトを構築します。
     * 
     * @param lines    テキストデータ
     * @param begin    開始位置情報
     * @param policy   コメント削除ポリシー
     * @param comment  コメント開始文字列
     */
    public CommentParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy,
            String comment) {
        super(lines, begin, policy);
        comment_ = comment;
    }

    /**
     * コメント開始文字列を取得します。
     * 
     * @return コメント開始文字列
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
     * このオブジェクトの解析・処理範囲のコメント開始文字列を除いたすべての文字列を取得します。
     * 
     * @return 解析・処理範囲のすべての文字列
     */
    protected List<String> getAllStringWithoutCommentString() {
        List<String> lines = getAllString();
        lines.set(0, lines.get(0).substring(comment_.length()));
        return lines;
    }

    /**
     * このオブジェクトのコメントが削除対象かどうか判定します。
     * 
     * @return 削除対象の場合は {@code true}
     */
    protected boolean willRemove() {
        // 一度判定している場合はその結果を返す
        if (canRemove_ != null) {
            return canRemove_;
        }

        List<String> lines = getAllStringWithoutCommentString();

        // 削除対象ポリシー確認
        Pattern rp = policy_.getRemovePattern();
        if (rp != null) {
            canRemove_ = lines.stream().anyMatch(e -> rp.matcher(e).find());
            return canRemove_;
        }

        // 削除対象外ポリシー確認
        Pattern ep = policy_.getExceptPattern();
        if (ep != null) {
            canRemove_ = !lines.stream().anyMatch(e -> ep.matcher(e).find());
            return canRemove_;
        }
        canRemove_ = true;
        return canRemove_;
    }
}
