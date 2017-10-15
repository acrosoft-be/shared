package be.acrosoft.gaia.shared.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcessTool.
 */
public class ProcessTool
{
  /**
   * PipeReader.
   */
  public static class PipeReader extends Thread
  {
    private InputStream _input;
    private ByteArrayOutputStream _target;
    
    /**
     * Create a new PipeReader.
     * @param input input stream.
     */
    public PipeReader(InputStream input)
    {
      _input=new BufferedInputStream(input);
      _target=new ByteArrayOutputStream();
      setDaemon(true);
      start();
    }
    
    @Override
    public void run()
    {
      try
      {
        byte[] buffer=new byte[4096];
        while(true)
        {
          int read=_input.read(buffer);
          if(read<=0)
            return;
          _target.write(buffer,0,read);
        }
      }
      catch(IOException ex)
      {
        ex.printStackTrace();
      }
    }
    
    /**
     * Get the byte array data.
     * @return byte array data.
     */
    public byte[] getData()
    {
      try
      {
        join();
      }
      catch(InterruptedException ex)
      {
        throw new RuntimeException(ex);
      }
      return _target.toByteArray();
    }
    
    /**
     * Get the data as list of strings.
     * @return data as strings.
     * @throws IOException in case of error.
     */
    public List<String> getStrings() throws IOException
    {
      List<String> ans=new ArrayList<String>();
      Charset encoding=Charset.defaultCharset();
      if(Platform.isWindows())
      {
        encoding=Charset.forName("cp850"); //$NON-NLS-1$
      }
      BufferedReader out=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getData()),encoding));
      String line=out.readLine();
      while(line!=null)
      {
        ans.add(line);
        line=out.readLine();
      }
      return ans;
    }
  }
  
  /**
   * Get pipe readers for the given process.
   * @param process process.
   * @return running pipe readers for output and error streams.
   */
  public static PipeReader[] getReaders(Process process)
  {
    return new PipeReader[] {new PipeReader(process.getInputStream()),new PipeReader(process.getErrorStream())};
  }
  
  /**
   * ProcessResult.
   */
  public static class ProcessResult
  {
    /**
     * Output.
     */
    public List<String> output;
    /**
     * Error.
     */
    public List<String> error;
    /**
     * Return code.
     */
    public int result;
    
  }
  
  /**
   * Execute the given process synchronously.
   * @param command command to execute.
   * @return process result.
   * @throws IOException in case of problems.
   */
  public static ProcessResult execute(String command) throws IOException
  {
    return execute(command,null);
  }
  
  /**
   * Execute the given process synchronously.
   * @param command command to execute.
   * @param envp environment variables.
   * @return process result.
   * @throws IOException in case of problems.
   */
  public static ProcessResult execute(String command,String[] envp) throws IOException
  {
    ProcessResult ans=new ProcessResult();
    Process process=Runtime.getRuntime().exec(command,envp);
    PipeReader[] readers=getReaders(process);
    try
    {
      ans.result=process.waitFor();
    }
    catch(InterruptedException ex)
    {
      throw new IOException(ex);
    }
    ans.output=readers[0].getStrings();
    ans.error=readers[1].getStrings();
    
    return ans;
  }
}
