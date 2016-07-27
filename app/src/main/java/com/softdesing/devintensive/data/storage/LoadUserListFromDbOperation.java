package com.softdesing.devintensive.data.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesing.devintensive.data.storage.models.User;
import com.softdesing.devintensive.data.storage.models.UserDao;
import com.softdesing.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.List;

public class LoadUserListFromDbOperation extends ChronosOperation<List<User>> {
    /**
     * The method for performing business-logic related work. Can contain time-consuming calls, but
     * should not perform any interaction with the UI, as it will be launched not in the Main
     * Thread.<br>
     * <p/>
     * All exceptions thrown in this method will be encapsulated in OperationResult object, so it
     * will not cause app crash.
     *
     * @return the result of the operation.
     */
    @Nullable
    @Override
    public List<User> run() {
        List<User> userList = new ArrayList<>();
        try {
            userList = DevintensiveApplication.getDaoSession().queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();

        } catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * This method returns a subclass of OperationResult class, related to the particular Operation
     * subclass, so that Chronos clients can distinguish results from different operations.
     *
     * @return OperationResult subclass, that will be created after the operation is complete.
     */
    @NonNull
    @Override
    public Class<Result> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<List<User>> {
        // usually this class is empty, but you may add some methods to customize its behavior
        // however, it must have a public constructor with no arguments
    }
}
