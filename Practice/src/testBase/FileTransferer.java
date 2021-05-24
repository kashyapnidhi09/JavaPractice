// Copyright 2020 Epsilon Data Management, LLC.  All rights reserved.
package testBase;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.Response;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FilePermission;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;


/**
 * Transfers files using the SSHJ library:
 * https://github.com/hierynomus/sshj SSHJ!!!
 * https://github.com/hierynomus/sshj/tree/master/examples/src/main/java/net/schmizz/sshj/examples
 *
 */
public class FileTransferer
{
    private final String streamsets_hostname;
    private final String streamsets_unixAccountUserName;
    private final String streamsets_user_libs_dir;
    private final String streamsets_unixAccountPrivateKeyDir;
    private final String streamsets_unixAccountPrivateKey;
    private final String streamsets_runtime_properties_dir;
    private final String copied_dependency_directory;
    private final String project_build_target;
    private final String streamsetsLibsExtrasDir = "/opt/streamsets-libs-extras";
    private final String groovyStageLibDirName = "streamsets-datacollector-groovy_2_4-lib";

    private final int usrGrpRWXOtherRXFilePermMask;
    private final RemoteResourceFilter isJarFileFilter = rri -> rri.isRegularFile() && rri.getName().endsWith( ".jar" );
    private final RemoteResourceFilter isLibDirFilter = rri -> rri.isDirectory() && rri.getName().equals( "lib" );

    public FileTransferer()
    {
        this.streamsets_hostname = System.getProperty("streamsets_hostname");
        this.streamsets_unixAccountUserName = System.getProperty("streamsets_unixAccountUserName");
        this.streamsets_user_libs_dir = System.getProperty("streamsets_user_libs_dir");
        this.streamsets_unixAccountPrivateKeyDir = System.getProperty("streamsets_unixAccountPrivateKeyDir");
        this.streamsets_unixAccountPrivateKey = System.getProperty("streamsets_unixAccountPrivateKey");;
        this.copied_dependency_directory = System.getProperty("copied_dependency_directory");;
        this.streamsets_runtime_properties_dir = System.getProperty("streamsets_runtime_properties_dir");;
        this.project_build_target = System.getProperty("project_build_target");;

        Set<FilePermission> usrGrpRWXOtherRXFilePermSet = new HashSet<>();
        usrGrpRWXOtherRXFilePermSet.add( FilePermission.USR_RWX );
        usrGrpRWXOtherRXFilePermSet.add( FilePermission.GRP_RWX );
        usrGrpRWXOtherRXFilePermSet.add( FilePermission.OTH_R );
        usrGrpRWXOtherRXFilePermSet.add( FilePermission.OTH_X );
        this.usrGrpRWXOtherRXFilePermMask = FilePermission.toMask( usrGrpRWXOtherRXFilePermSet );
    }


    public static void main( String[] args )
    {
/*
        System.setProperty( "streamsets_hostname", "dc1udatasfr01");
        System.setProperty( "streamsets_unixAccountUserName", "svc_ss_sfr");
        System.setProperty( "streamsets_user_libs_dir", "/opt/streamsets-user-libs");
        System.setProperty( "streamsets_unixAccountPrivateKeyDir", "C:\\dev\\workspaces\\salesforce-replication\\salesforce-replication-installer");
        System.setProperty( "streamsets_unixAccountPrivateKey", "streamsets-salesforce-replication-dev_rsa");
        System.setProperty( "copied_dependency_directory", "C:\\dev\\workspaces\\salesforce-replication\\salesforce-replication-installer\\target\\salesforce-getupdated-stages");
        System.setProperty( "streamsets_runtime_properties_dir", "/etc/sdc/runtime");
        System.setProperty( "project_build_target", "C:\\dev\\workspaces\\salesforce-replication\\salesforce-replication-installer\\target\\classes");
*/

        FileTransferer ft = new FileTransferer();
        try
        {
            ft.transferFiles();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            throw new RuntimeException("Failed transferring files. ", t );
        }
    }

    private void transferFiles() throws IOException, ArchiveException
    {
        try ( SSHClient ssh = new SSHClient() )
        {
            configSSHClient( ssh );

            try ( SFTPClient sftpClient = ssh.newSFTPClient() )
            {
                transferRuntimePropertiesFile( sftpClient );
                transferCustomStages( sftpClient );
                transferCommonLibraryToLibsExtrasDir( sftpClient );
            }
        }
    }

    private void transferCustomStages( SFTPClient sftpClient ) throws IOException, ArchiveException
    {

        String[] stageNames = new String[]{ "salesforce-getupdated-processor", "salesforce-getupdated-origin" };
        for ( String stageName : stageNames )
        {
            deleteCustomStage( sftpClient, stageName );
            createCustomStageDirectories( sftpClient, stageName );
        }

        Path tarGzipDirPath = Paths.get( copied_dependency_directory );
        File tarGzipFile = tarGzipDirPath.toFile();
        Stream<Path> pathStream = Files.find(  tarGzipDirPath, Integer.MAX_VALUE,
                ( path, basicFileAttributes ) -> path.toFile().getName().matches( ".*.tar.gz" ) );
        List<Path> filesToExplode = pathStream.collect( Collectors.toList() );

        for ( Path toExplode : filesToExplode )
        {
            File tarFile = unGzip(toExplode.toFile(), tarGzipFile );
            toExplode.toFile().deleteOnExit();
            List<File> tarFileList = unTar( tarFile, tarGzipFile );
            tarFile.deleteOnExit();
            System.out.print( "Sftp'ing " + tarFileList.size() + " files" );

            for ( File toCopy : tarFileList )
            {
                Path p = toCopy.toPath();
                String stageNameDir = p.getName( p.getNameCount() - 3 ).toString();
                String customDir = streamsets_user_libs_dir + "/" + stageNameDir + "/lib";
                FileSystemFile destFile = new FileSystemFile( toCopy );
                sftpClient.put( destFile, customDir );
                sftpClient.chmod( customDir + "/" + destFile.getName(), usrGrpRWXOtherRXFilePermMask );
                System.out.print(".");
            }
            System.out.println();
        }
    }

    private void createCustomStageDirectories( SFTPClient sftpClient, String stageName ) throws IOException
    {
        String stageDir = streamsets_user_libs_dir + "/" + stageName;
        sftpClient.mkdir( stageDir );
        sftpClient.chmod( stageDir, usrGrpRWXOtherRXFilePermMask );
        String libDir = stageDir + "/lib";
        sftpClient.mkdir( libDir );
        sftpClient.chmod( libDir, usrGrpRWXOtherRXFilePermMask );
    }

    private void deleteCustomStage( SFTPClient sftpClient, String stageName ) throws IOException
    {
        System.out.println("deleteCustomStage: " + stageName );
        List<RemoteResourceInfo> toDeleteList = new ArrayList<>();
        RemoteResourceFilter isStageDirFilter = rri -> rri.isDirectory() && rri.getName().equals( stageName );
        try
        {
            // stage directory
            String userLibsDir = streamsets_user_libs_dir;
            List<RemoteResourceInfo> remoteListingResults = sftpClient.ls( userLibsDir, isStageDirFilter );
            toDeleteList.addAll( remoteListingResults );
            System.out.println( "1 - toDeleteList = " + toDeleteList );
            if ( toDeleteList.isEmpty() )
            {
                return;
            }

            // lib directory
            String stageInstallDir = userLibsDir + "/" + stageName;
            remoteListingResults = sftpClient.ls( stageInstallDir, isLibDirFilter );
            toDeleteList.addAll( remoteListingResults );
            System.out.println( "2 - toDeleteList = " + toDeleteList );

            if ( toDeleteList.isEmpty() )
            {
                return;
            }

            // jars in lib dir
            remoteListingResults = sftpClient.ls( stageInstallDir + "/lib", isJarFileFilter );

            toDeleteList.addAll( remoteListingResults );
            System.out.println( "3 - toDeleteList = " + toDeleteList );
            if ( toDeleteList.isEmpty() )
            {
                return;
            }
        }
        catch ( SFTPException sftpException )
        {
            Response.StatusCode sc = sftpException.getStatusCode();
            if ( !( sc.getCode() == Response.StatusCode.NO_SUCH_FILE.getCode() || sc.getCode() == Response.StatusCode.NO_SUCH_PATH.getCode() ) )
            {
                throw sftpException;
            }
            else
            {
                sftpException.printStackTrace();
            }
        }
        // reverse order so files/dirs in furthest down directory are removed first
        Collections.reverse( toDeleteList );

        System.out.println( "4 - toDeleteList = " + toDeleteList );
        if ( toDeleteList.isEmpty() )
        {
            System.out.println( "Nothing to remove for " + stageName );
        }
        for ( RemoteResourceInfo toDelete : toDeleteList )
        {
            if ( toDelete.isRegularFile() )
            {
                System.out.println( "About to delete file = " + toDelete.getPath() );
                sftpClient.rm( toDelete.getPath() );
            }
            else
            {
                System.out.println( "About to delete directory: " + toDelete );
                sftpClient.rmdir( toDelete.getPath() );
            }
        }
    }

    private void transferRuntimePropertiesFile( SFTPClient sftpClient ) throws IOException
    {
        String runtimePropFileName = "salesforce-replication.properties";
        System.out.println("About to copy " + project_build_target + "/" + runtimePropFileName + " to " + streamsets_runtime_properties_dir );
        Path runtimePropFilePath = Paths.get( project_build_target, runtimePropFileName );
        if( ! Files.exists( runtimePropFilePath ) )
        {
            throw new RuntimeException("Failed to find " + runtimePropFilePath );
        }
        File rppFile = runtimePropFilePath.toFile();
        String rppRemotePath = streamsets_runtime_properties_dir + "/" + rppFile.getName();
        if ( sftpClient.statExistence( rppRemotePath ) != null )
        {
            sftpClient.rm( rppRemotePath );
        }
        sftpClient.put( new FileSystemFile( rppFile ), streamsets_runtime_properties_dir );
        sftpClient.chmod( rppRemotePath, usrGrpRWXOtherRXFilePermMask );
    }

    private void transferCommonLibraryToLibsExtrasDir( SFTPClient sftpClient ) throws IOException
    {
        deleteCommonLibrary( sftpClient );
        createCommonLibraryDirectories( sftpClient );

        Path jarDirPath = Paths.get( copied_dependency_directory );
        Stream<Path> pathStream = Files.find(  jarDirPath, Integer.MAX_VALUE,
                ( path, basicFileAttributes ) -> path.toFile().getName().matches( "salesforce-getupdated-common.*.jar" ) );
        List<Path> filesToCopy = pathStream.collect( Collectors.toList() );
        for ( Path toCopy : filesToCopy )
        {
            String dir = streamsetsLibsExtrasDir + "/" + groovyStageLibDirName + "/lib";
            FileSystemFile destFile = new FileSystemFile( toCopy.toFile() );
            sftpClient.put( destFile, dir );
            sftpClient.chmod( dir + "/" + destFile.getName(), usrGrpRWXOtherRXFilePermMask );
            System.out.print(".");
        }
    }

    private void deleteCommonLibrary( SFTPClient sftpClient ) throws IOException
    {
        List<RemoteResourceInfo> toDeleteList = new ArrayList<>();
        RemoteResourceFilter isStageDirFilter = rri -> rri.isDirectory() && rri.getName().equals( groovyStageLibDirName );
        try
        {
            // stage directory
            String userLibsDir = streamsetsLibsExtrasDir;
            List<RemoteResourceInfo> remoteListingResults = sftpClient.ls( userLibsDir, isStageDirFilter );
            toDeleteList.addAll( remoteListingResults );
            System.out.println( "1 - toDeleteList = " + toDeleteList );
            if ( toDeleteList.isEmpty() )
            {
                return;
            }

            // lib directory
            String stageInstallDir = userLibsDir + "/" + groovyStageLibDirName;
            remoteListingResults = sftpClient.ls( stageInstallDir, isLibDirFilter );
            toDeleteList.addAll( remoteListingResults );
            System.out.println( "2 - toDeleteList = " + toDeleteList );

            if ( toDeleteList.isEmpty() )
            {
                return;
            }

            // jars in lib dir
            remoteListingResults = sftpClient.ls( stageInstallDir + "/lib", isJarFileFilter );

            toDeleteList.addAll( remoteListingResults );
            System.out.println( "3 - toDeleteList = " + toDeleteList );
            if ( toDeleteList.isEmpty() )
            {
                return;
            }
        }
        catch ( SFTPException sftpException )
        {
            Response.StatusCode sc = sftpException.getStatusCode();
            if ( !( sc.getCode() == Response.StatusCode.NO_SUCH_FILE.getCode() || sc.getCode() == Response.StatusCode.NO_SUCH_PATH.getCode() ) )
            {
                throw sftpException;
            }
            else
            {
                sftpException.printStackTrace();
            }
        }
        // reverse order so files/dirs in furthest down directory are removed first
        Collections.reverse( toDeleteList );

        System.out.println( "4 - toDeleteList = " + toDeleteList );
        if ( toDeleteList.isEmpty() )
        {
            System.out.println( "Nothing to remove for " + groovyStageLibDirName );
        }
        for ( RemoteResourceInfo toDelete : toDeleteList )
        {
            if ( toDelete.isRegularFile() )
            {
                System.out.println( "About to delete file = " + toDelete.getPath() );
                sftpClient.rm( toDelete.getPath() );
            }
            else
            {
                System.out.println( "About to delete directory: " + toDelete );
                sftpClient.rmdir( toDelete.getPath() );
            }
        }
    }

    private void createCommonLibraryDirectories( SFTPClient sftpClient ) throws IOException
    {
        String dir = streamsetsLibsExtrasDir + "/" + groovyStageLibDirName;
        sftpClient.mkdir( dir );
        sftpClient.chmod( dir, usrGrpRWXOtherRXFilePermMask );
        String libDir = dir + "/lib";
        sftpClient.mkdir( libDir );
        sftpClient.chmod( libDir, usrGrpRWXOtherRXFilePermMask );
    }


    private void configSSHClient( SSHClient ssh ) throws IOException
    {
        ssh.addHostKeyVerifier( new PromiscuousVerifier() );
        ssh.connect( streamsets_hostname );
        String privateKeyDir = streamsets_unixAccountPrivateKeyDir;
        if ( streamsets_unixAccountPrivateKeyDir.contains( "~" ) )
        {
            privateKeyDir = streamsets_unixAccountPrivateKeyDir.replace( "~", System.getProperty( "user.home" ) );
        }
        Path privateKey = Paths.get( privateKeyDir, streamsets_unixAccountPrivateKey );
        if( ! Files.exists( privateKey ) )
        {
            throw new RuntimeException("Failed to find ssh private key.  This will prevent logging in.  Your environment is not configured correctly.  Looking for: " + privateKey );
        }
        ssh.authPublickey( streamsets_unixAccountUserName, privateKey.toString() );
    }

    /** Untar an input file into an output file.

     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.tar' extension.
     *
     * @param inputFile     the input .tar file
     * @param outputDir     the output directory file.
     * @throws IOException
     * @throws FileNotFoundException
     *
     * @return  The {@link List} of {@link File}s with the untared content.
     * @throws ArchiveException
     */
    private static List<File> unTar( final File inputFile, final File outputDir ) throws FileNotFoundException, IOException, ArchiveException
    {
        final List<File> untaredFiles = new LinkedList<File>();
        try ( final InputStream is = new FileInputStream( inputFile );
              final TarArchiveInputStream debInputStream = ( TarArchiveInputStream ) new ArchiveStreamFactory().createArchiveInputStream( "tar", is ) )
        {
            TarArchiveEntry entry = null;
            while ( ( entry = ( TarArchiveEntry ) debInputStream.getNextEntry() ) != null )
            {
                final File outputFile = new File( outputDir, entry.getName() );
                if ( entry.isDirectory() )
                {
                    if ( !outputFile.exists() )
                    {
                        if ( !outputFile.mkdirs() )
                        {
                            throw new IllegalStateException( String.format( "Couldn't create directory %s.", outputFile.getAbsolutePath() ) );
                        }
                    }
                }
                else
                {
                    if ( !outputFile.getParentFile().exists() )
                    {
                        outputFile.getParentFile().mkdirs();
                    }
                    try ( final OutputStream outputFileStream = new FileOutputStream( outputFile ) )
                    {
                        org.apache.commons.io.IOUtils.copy( debInputStream, outputFileStream );
                    }
                }
                untaredFiles.add( outputFile );
            }
        }

        return untaredFiles;
    }

    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension.
     *
     * @param inputFile     the input .gz file
     * @param outputDir     the output directory file.
     * @throws IOException
     * @throws FileNotFoundException
     *
     * @return  The {@File} with the ungzipped content.
     */
    private static File unGzip( final File inputFile, final File outputDir ) throws FileNotFoundException, IOException
    {
        final File outputFile = new File( outputDir, inputFile.getName().substring( 0, inputFile.getName().length() - 3 ) );

        try ( final GZIPInputStream in = new GZIPInputStream( new FileInputStream( inputFile ) );
              final FileOutputStream out = new FileOutputStream( outputFile ) )
        {
            org.apache.commons.io.IOUtils.copy( in, out );
        }

        return outputFile;
    }

//                commandsToRun.forEach( System.out::println );
//                for ( String commandToRun : commandsToRun )
//                {
//                    net.schmizz.sshj.connection.channel.direct.Session session = ssh.startSession();
//                    net.schmizz.sshj.connection.channel.direct.Session.Command pwd = session.exec( commandToRun );
//                    System.out.println( "IOUtils.readFully(cmd.getInputStream()).toString() = " + IOUtils.readFully( pwd.getInputStream() ).toString() );
//                    pwd.join(5, TimeUnit.SECONDS );
//                    System.out.println("\n** exit status: " + pwd.getExitStatus());
//                }


}