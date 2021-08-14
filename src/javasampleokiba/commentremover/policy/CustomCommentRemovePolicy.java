package javasampleokiba.commentremover.policy;

import java.util.regex.Pattern;

/**
 * �J�X�^�}�C�Y�p�̃R�����g�폜�|���V�[�N���X.
 */
public class CustomCommentRemovePolicy implements CommentRemovePolicy {

    private String[] commentString_         = null;
    private String[] multiLineCommentBegin_ = null;
    private String[] multiLineCommentEnd_   = null;
    private boolean enabledRemoveEmptyLine_ = false;
    private boolean enabledRemoveBlankLine_ = false;
    private Pattern blankPattern_           = null;
    private String[] quotationMarks_        = null;
    private Pattern removePattern_          = null;
    private Pattern exceptPattern_          = null;

    /**
     * ���ׂĂ̐ݒ肪�N���A���ꂽ�I�u�W�F�N�g���\�z���܂��B
     */
    public CustomCommentRemovePolicy() {
    }

    /**
     * �w�肳�ꂽ�|���V�[�̐ݒ�������p�����I�u�W�F�N�g���\�z���܂��B
     * 
     * @param policy  �ݒ�������p���R�����g�폜�|���V�[
     */
    public CustomCommentRemovePolicy(CommentRemovePolicy policy) {
        commentString_ = policy.getCommentString();
        multiLineCommentBegin_ = policy.getMultiLineCommentBegin();
        multiLineCommentEnd_ = policy.getMultiLineCommentEnd();
        enabledRemoveEmptyLine_ = policy.isEnabledRemoveEmptyLine();
        enabledRemoveBlankLine_ = policy.isEnabledRemoveBlankLine();
        blankPattern_ = policy.getBlankPattern();
        quotationMarks_ = policy.getQuotationMarks();
        removePattern_ = policy.getRemovePattern();
        exceptPattern_ = policy.getExceptPattern();
    }

    @Override
    public String[] getCommentString() {
        return commentString_;
    }

    /**
     * �P��s�R�����g�̊J�n�������ݒ肵�܂��i�����w��j�B
     * {@code null}�܂��͋�z���ݒ肷��ƁA�P��s�R�����g�폜�������ɂȂ�܂��B
     * 
     * @param commentString  �R�����g�J�n������
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setCommentString(String[] commentString) {
        commentString_ = commentString;
        return this;
    }

    @Override
    public String[] getMultiLineCommentBegin() {
        return multiLineCommentBegin_;
    }

    /**
     * �����s�R�����g�̊J�n�������ݒ肵�܂��i�����w��j�B
     * {@code null}�܂��͋�z���ݒ肷��ƁA�����s�R�����g�폜�������ɂȂ�܂��B
     * 
     * @param multiLineCommentBegin  �����s�R�����g�J�n������
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setMultiLineCommentBegin(String[] multiLineCommentBegin) {
        multiLineCommentBegin_ = multiLineCommentBegin;
        return this;
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return multiLineCommentEnd_;
    }

    /**
     * �����s�R�����g�̏I���������ݒ肵�܂��i�����w��j�B
     * {@code null}�܂��͋�z���ݒ肷��ƁA�����s�R�����g�폜�������ɂȂ�܂��B
     * �������w�肷��ꍇ�́A{@link #setMultiLineCommentBegin(String[])}�Ə��Ԃ����킹�Ă��������B
     * 
     * @param multiLineCommentEnd  �����s�R�����g�I��������
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setMultiLineCommentEnd(String[] multiLineCommentEnd) {
        multiLineCommentEnd_ = multiLineCommentEnd;
        return this;
    }

    @Override
    public boolean isEnabledRemoveEmptyLine() {
        return enabledRemoveEmptyLine_;
    }

    /**
     * �R�����g�폜�ɂ���ċ�s�ƂȂ����s���폜���邩�ݒ肵�܂��B
     * 
     * @param enabledRemoveEmptyLine  ��s���폜����ꍇ�� {@code true}
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setEnabledRemoveEmptyLine(boolean enabledRemoveEmptyLine) {
        enabledRemoveEmptyLine_ = enabledRemoveEmptyLine;
        return this;
    }

    @Override
    public boolean isEnabledRemoveBlankLine() {
        return enabledRemoveBlankLine_;
    }

    /**
     * �R�����g�폜�ɂ���ċ󔒍s�ƂȂ����s���폜���邩�ݒ肵�܂��B
     * {@link #setBlankPattern(Pattern)}�Őݒ肳�ꂽ��������󔒕����Ƃ݂Ȃ��܂��B
     * 
     * @param enabledRemoveBlankLine  �󔒍s���폜����ꍇ�� {@code true}
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setEnabledRemoveBlankLine(boolean enabledRemoveBlankLine) {
        enabledRemoveBlankLine_ = enabledRemoveBlankLine;
        return this;
    }

    @Override
    public Pattern getBlankPattern() {
        return blankPattern_;
    }

    /**
     * �󔒕����Ƃ݂Ȃ�������i���K�\���j��ݒ肵�܂��B
     * {@code null}��ݒ肷��Ƌ󔒕����w��Ȃ��ɂȂ�܂��B
     * 
     * @param blankPattern  �󔒕����i���K�\���j
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setBlankPattern(Pattern blankPattern) {
        blankPattern_ = blankPattern;
        return this;
    }

    @Override
    public String[] getQuotationMarks() {
        return quotationMarks_;
    }

    /**
     * ���p���������ݒ肵�܂��i�����w��j�B
     * {@code null}��ݒ肷��ƈ��p��������w��Ȃ��ɂȂ�܂��B
     * 
     * @param quotationMarks  ���p��������
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setQuotationMarks(String[] quotationMarks) {
        quotationMarks_ = quotationMarks;
        return this;
    }

    @Override
    public Pattern getRemovePattern() {
        return removePattern_;
    }

    /**
     * �R�����g�폜���̍폜�Ώۂ̃R�����g�i���K�\���j��ݒ肵�܂��B
     * {@code null}��ݒ肷��Ǝw��Ȃ��ɂȂ�A���ׂẴR�����g���폜�ΏۂɂȂ�܂��B
     * 
     * @param removePattern  �폜�Ώۂ̃R�����g�i���K�\���j
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setRemovePattern(Pattern removePattern) {
        removePattern_ = removePattern;
        return this;
    }

    @Override
    public Pattern getExceptPattern() {
        return exceptPattern_;
    }

    /**
     * �R�����g�폜���̍폜�ΏۊO�̃R�����g�i���K�\���j��ݒ肵�܂��B
     * {@code null}��ݒ肷��Ǝw��Ȃ��ɂȂ�A���ׂẴR�����g���폜�ΏۂɂȂ�܂��B
     * {@link #setRemovePattern(Pattern)}���w�肳��Ă���ꍇ�͂����炪�D�悳��܂��B
     * 
     * @param exceptPattern  �폜�ΏۊO�̃R�����g�i���K�\���j
     * @return ���̃I�u�W�F�N�g�̎Q��
     */
    public CustomCommentRemovePolicy setExceptPattern(Pattern exceptPattern) {
        exceptPattern_ = exceptPattern;
        return this;
    }
}