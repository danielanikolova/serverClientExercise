package lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CopyProcessor
{

	private DataOutputStream output = null;
	private DataInputStream input = null;

	public CopyProcessor(DataInputStream input, DataOutputStream output)
	{
		this.input = input;
		this.output = output;
	}

	public void readFile(String source)
	{

		BufferedReader br = null;

		if (source == null)
			throw new IllegalArgumentException("File name cannot be null.");
		File sourceFile = new File(source);

		String fileName = sourceFile.getName();

		/*
		 * first we send the file name, then we begin to read the file content line by
		 * line and send it until we receive null after read line; Then we send
		 * FILE_TRANSFER_FINISHED
		 */
		try
		{
			br = new BufferedReader(new FileReader(sourceFile));

			output.writeUTF(FTPProtocolHandler.START_COPY_PROCESSOR_WRITE + "<" + fileName + ">");
			String fileLine = br.readLine();

			while (fileLine != null)
			{
				output.writeUTF(fileLine);
				fileLine = br.readLine();
			}

			output.writeUTF(FTPProtocolHandler.FILE_SENT);

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void writeFile(String fileName)
	{

		BufferedWriter bw = null;
		String fileLine;
		try
		{
			File destinationFile = new File(
					"C:\\Users\\danie\\Desktop\\serverClientExercise\\destination\\" + fileName);

			bw = new BufferedWriter(new FileWriter(destinationFile));

			fileLine = input.readUTF();
			while (fileLine != null)
			{
				if (fileLine.equals(FTPProtocolHandler.FILE_SENT))
				{
					break;
				}
				bw.write(fileLine);
				bw.write(System.lineSeparator());
				bw.flush();
				fileLine = input.readUTF();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	// public void closeBuffer()
	// {
	// try
	// {
	// br.close();
	// bw.close();
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }

}
