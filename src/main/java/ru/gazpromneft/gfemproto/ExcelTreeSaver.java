package ru.gazpromneft.gfemproto;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelTreeSaver {

    private static final Logger logger = Logger.getLogger(ExcelTreeSaver.class.getName());
    private static int lockWaitingPeriod = 1;               // seconds
    private static int totalLockWaitingTime = 15;           // seconds
    private static int totalLockReadingWaitingTime = 5;     // seconds

    public static void configure(int lockWaitingPeriod,
                                 int totalLockWaitingTime,
                                 int totalLockReadingWaitingTime) {
        ExcelTreeSaver.lockWaitingPeriod = lockWaitingPeriod;
        ExcelTreeSaver.totalLockWaitingTime = totalLockWaitingTime;
        ExcelTreeSaver.totalLockReadingWaitingTime = totalLockReadingWaitingTime;
    }

    public static void toFile(ExcelTreeModel model, File file) throws IOException {
        logger.info("Started saving state");

        FileOutputStream outputStream = new FileOutputStream(file);
        logger.info("Created file output stream");
        FileChannel channel = outputStream.getChannel();
        FileLock lock = repeatedTryLock(channel);
        logger.info("State file lock obtained");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        logger.info("Created object output stream");
        // сохраняем дерево в файл
        objectOutputStream.writeObject(model);
        logger.info("Saved tree model to output stream");
        //закрываем поток и освобождаем ресурсы
        lock.release();
        logger.info("State file lock released");
        objectOutputStream.close();
        logger.info("Closed output stream");
    }

    public static ExcelTreeModel fromFile(File file) throws IOException, ClassNotFoundException {
        waitForUnlock();
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        ExcelTreeModel savedTreeModel = (ExcelTreeModel) objectInputStream.readObject();
        objectInputStream.close();
        return savedTreeModel;
    }

    private static FileLock repeatedTryLock(FileChannel channel) throws OverlappingFileLockException {

        for (int waitingTime = 0; waitingTime < totalLockWaitingTime; waitingTime += lockWaitingPeriod) {
            try {
                return channel.tryLock();
            } catch (OverlappingFileLockException | IOException ofe) {
                logger.info("File " + Conventions.STATE_FILE_NAME + " is locked. Waiting...");
            }


            try {
                Thread.sleep(lockWaitingPeriod * 1000L);
            } catch (InterruptedException ie) {
                logger.log(Level.SEVERE, ie.getMessage(), ie);
                throw new OverlappingFileLockException();
            }
        }
        throw new OverlappingFileLockException();
    }

    private static void waitForUnlock() throws IOException {
        int waitingTime = 0;
        while (waitingTime < totalLockReadingWaitingTime) {

            try (FileInputStream input = new FileInputStream(Conventions.STATE_FILE_NAME)){
                input.read();
                logger.info("State file has no lock on it");
                return;
            } catch (IOException e) {
                logger.info("Waiting for state file to be released");
            }

            waitingTime += lockWaitingPeriod;
            try {
                Thread.sleep(lockWaitingPeriod * 1000L);
            } catch (InterruptedException ie) {
                logger.log(Level.SEVERE, ie.getMessage(), ie);
                throw new IOException();
            }
        }
        throw new OverlappingFileLockException();
    }
}
