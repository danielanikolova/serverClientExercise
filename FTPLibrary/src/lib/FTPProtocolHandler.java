package lib;

public interface FTPProtocolHandler {

	static final String FILE_ACCEPTED = "200 File accepted";
	static final String FILE_SENT = "200 File sent";
	static final String PROVIDING_FILE_CONTENT = "200 Providing file content";
	static final String START_COPY_PROCESSOR_WRITE = "Start writing file";
	static final String START_COPY_PROCESSOR_READ = "Start reading file";
}
