// Copyright 2024 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.base.test.transit;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import org.chromium.base.TimeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The return value of {@link Condition#check()}.
 *
 * <p>Includes whether the condition is fulfilled, an optional message and the timestamp of the
 * check.
 */
public class ConditionStatus {

    private static final int TRUNCATE_STATUS_UPDATE = 300;

    /** Lifecycle phases of ConditionalState. */
    @IntDef({
        ConditionStatus.Status.NOT_FULFILLED,
        ConditionStatus.Status.FULFILLED,
        ConditionStatus.Status.ERROR
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int NOT_FULFILLED = 0;
        int FULFILLED = 1;
        int ERROR = 2;
    }

    private final long mTimestamp;
    private final @Status int mStatus;
    private @Nullable String mMessage;

    ConditionStatus(@Status int status, @Nullable String message) {
        mTimestamp = TimeUtils.currentTimeMillis();
        mStatus = status;
        if (message != null) {
            if (message.length() > TRUNCATE_STATUS_UPDATE) {
                mMessage = message.substring(0, TRUNCATE_STATUS_UPDATE);
            } else {
                mMessage = message;
            }
        }
    }

    public boolean isFulfilled() {
        return mStatus == Status.FULFILLED;
    }

    public boolean isError() {
        return mStatus == Status.ERROR;
    }

    public @Status int getStatus() {
        return mStatus;
    }

    public @Nullable String getMessage() {
        return mMessage;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    void amendMessage(String otherMessage) {
        if (mMessage != null) {
            mMessage = mMessage + "  " + otherMessage;
        } else {
            mMessage = otherMessage;
        }
    }

    String getMessageAsGate() {
        StringBuilder fullMessage = new StringBuilder();
        fullMessage.append("<Gate: ");
        String statusMessage =
                switch (mStatus) {
                    case Status.FULFILLED -> "REQUIRED";
                    case Status.NOT_FULFILLED -> "NOT REQ";
                    case Status.ERROR -> "ERROR";
                    default -> throw new IllegalStateException();
                };
        fullMessage.append(statusMessage);
        if (mMessage != null) {
            fullMessage.append(" | ");
            fullMessage.append(mMessage);
        }
        fullMessage.append(">");
        return fullMessage.toString();
    }
}
