package com.nickdoran.bungee.dynamicservercreation.util;

import com.nickdoran.bungee.dynamicservercreation.reply.AbstractDSCReply;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public interface Callback<T extends AbstractDSCReply> {
    void onCallback(T result);
}
