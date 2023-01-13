package com.wooaooha.helloworld.gradleplugin;

import org.gradle.api.provider.Property;

public interface GreetingPluginExtension {

    Property<String> getMessage();
    Property<String> getGreeter();

}
