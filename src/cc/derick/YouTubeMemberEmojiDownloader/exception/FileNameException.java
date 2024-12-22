package cc.derick.YouTubeMemberEmojiDownloader.exception;

public class FileNameException extends Exception {
	
    private ErrorType errorType;

    public enum ErrorType {
        FILE_NAME_TOO_LONG("文件名稱過長"),
        INVALID_FORMAT("文件名稱無效"),
        FILE_NAME_ALREADY_EXISTS("文件名稱已存在"),
        FILE_NAME_TOO_MANY_CONFLICTS("文件名稱衝突過多");

        private String errorMessage;

        ErrorType(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


    public FileNameException(ErrorType errorType) {
        super(errorType.getErrorMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
	
}
