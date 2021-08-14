package javasampleokiba.commentremover.parser;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javasampleokiba.commentremover.CommentRemover.Mode;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * ソースコードを解析する基底クラス.
 */
public class CodeParser {

    /** 解析対象のテキストデータ */
    protected final List<String> lines_;
    /** コメント削除ポリシー */
    protected final CommentRemovePolicy policy_;
    private final CodePosition begin_;
    private CodePosition end_;

    /**
     * オブジェクトを構築します。
     * 
     * @param lines   テキストデータ
     * @param begin   開始位置情報
     * @param policy  コメント削除ポリシー
     */
    public CodeParser(List<String> lines, CodePosition begin, CommentRemovePolicy policy) {
        lines_ = lines;
        begin_ = begin;
        policy_ = policy;
    }

    /**
     * 開始位置情報を取得します。
     * 
     * @return 開始位置情報
     */
    public CodePosition getBegin() {
        return begin_;
    }

    /**
     * 終了位置情報を取得します。
     * 
     * @return 終了位置情報
     */
    public CodePosition getEnd() {
        return end_;
    }

    /**
     * 終了位置情報を設定します。
     * 
     * @param end  終了位置情報
     */
    protected void setEnd(CodePosition end) {
        end_ = end;
    }

    /**
     * 指定された行位置におけるこのオブジェクトでの解析・処理結果の文字列を取得します。
     * 
     * @param row   行位置
     * @param mode  処理モード
     * @return 解析・処理結果の文字列
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
     * 指定された行位置における処理前のオリジナルの文字列を取得します。
     * 
     * @param row  行位置
     * @return 処理前のオリジナルの文字列
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
     * このオブジェクトの解析・処理範囲のすべての文字列を取得します。
     * 
     * @return 解析・処理範囲のすべての文字列
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
     * 指定された行位置がこのオブジェクトの解析・処理範囲内か判定します。
     * 
     * @param row  行位置
     * @return 解析・処理範囲内の場合は {@code true}
     */
    public boolean withinRow(int row) {
        return begin_.row() <= row && row <= end_.row();
    }

    /**
     * このオブジェクトの解析・処理範囲が空(文字も改行も持たない)であるか判定します。
     * 
     * @return 解析・処理範囲が空である場合は {@code true}
     */
    public boolean isEmpty() {
        return begin_.row() == end_.row() &&
                begin_.pos() == end_.pos();
    }

    /**
     * このオブジェクトの解析・処理範囲の次の範囲を担当するオブジェクトを生成して返します。
     * 
     * @return 次の範囲を担当するソースコード解析オブジェクト
     * @throws ParseException  解析エラーが発生した場合
     */
    public CodeParser next() throws ParseException {
        String[] comments = policy_.getCommentString();
        String[] beginComments = policy_.getMultiLineCommentBegin();
        String[] endComments = policy_.getMultiLineCommentEnd();
        String[] quotationMarks = policy_.getQuotationMarks();
        int beginPos = begin_.pos();

        // 次のコメント開始文字列、または引用符文字列が見つかるまでループ
        for (int row = begin_.row(); row < lines_.size(); row++) {
            String line = lines_.get(row);
            SimpleEntry<Integer, String> e;

            // 引用符文字列位置取得
            e = getIndexWithoutEscaped(line, beginPos, quotationMarks);
            int qmPos = e.getKey();
            String quotationMark = e.getValue();

            // コメント文字列位置取得
            e = getIndex(line, beginPos, comments);
            int cPos = e.getKey();
            String comment = e.getValue();

            // コメント開始＋終了文字列位置取得
            e = getIndex(line, beginPos, beginComments, endComments);
            int ccPos = e.getKey();
            String combinedComment = e.getValue();

            // コメント開始文字列位置取得
            e = getIndex(line, beginPos, beginComments);
            int bcPos = e.getKey();
            String beginComment = e.getValue();

            // 引用符文字列がコメント・コメント開始文字列よりも前にある？
            if (isAhead(qmPos, quotationMark, cPos, comment) &&
                isAhead(qmPos, quotationMark, bcPos, beginComment)) {
                end_ = new CodePosition(row, qmPos);
                return new LiteralParser(lines_, new CodePosition(row, qmPos), policy_,
                        quotationMark);
            }

            // コメント文字列がコメント開始文字列よりも前にある？
            if (isAhead(cPos, comment, bcPos, beginComment)) {
                end_ = new CodePosition(row, cPos);
                return new CommentParser(lines_, new CodePosition(row, cPos), policy_,
                        comment);
            }

            // コメント開始＋終了文字列がコメント開始文字列よりも前にある？
            if (isAhead(ccPos, bcPos)) {
                end_ = new CodePosition(row, ccPos);
                return new MutliLineCommentParser(lines_, new CodePosition(row, ccPos), policy_,
                        combinedComment);
            }

            // コメント開始文字列がある？
            if (0 <= bcPos) {
                end_ = new CodePosition(row, bcPos);
                return new MutliLineCommentParser(lines_, new CodePosition(row, bcPos), policy_,
                        beginComment);
            }

            // コメント・コメント開始文字列がないなら次の行へ
            beginPos = 0;
        }

        // 全行解析終了
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
     * 次のCodeParserオブジェクトを生成して返します。
     * 
     * @param row  行位置
     * @param pos  文字位置
     * @return 新しいCodeParserオブジェクト
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
     * 指定された1つ以上の検索文字列のうち、最も先頭に近い文字列の文字位置、文字列のペアを取得します。
     * 見つからない場合は、-1, {@code null}のペアを返します。
     * 
     * @param str      検索対象文字列
     * @param start    検索開始位置
     * @param targets  検索文字列
     * @return 文字位置、文字列のペア
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
     * 指定された1つ以上の検索文字列のうち、最も先頭に近い文字列の文字位置、文字列のペアを取得します。
     * 検索文字列は、コメント開始文字列とコメント終了文字列を結合した文字列です。
     * 見つからない場合は、-1, {@code null}のペアを返します。
     * 
     * @param str      検索対象文字列
     * @param start    検索開始位置
     * @param begins   コメント開始文字列
     * @param ends     コメント終了文字列
     * @return 文字位置、文字列のペア
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
     * 指定された1つ以上の検索文字列のうち、最も先頭に近い文字列の文字位置、文字列のペアを取得します。
     * 検索対象文字列が見つかった場合でも"\"によってエスケープされている場合はそれを無視します。
     * 
     * @param str      検索対象文字列
     * @param start    検索開始位置
     * @param targets  検索文字列
     * @return 文字位置、文字列のペア
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
            // 検索文字列の前のエスケープ文字の数をカウント
            for (int i = pos - 1; i >= start; i--) {
                if (str.charAt(i) == '\\') {
                    count++;
                } else {
                    break;
                }
            }
            // 偶数なら検索文字列がエスケープされているわけではないので、その位置を返却
            if (count % 2 == 0) {
                return pos;
            } else {
                // 検索文字列がエスケープされているので、さらに後続を検索
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
