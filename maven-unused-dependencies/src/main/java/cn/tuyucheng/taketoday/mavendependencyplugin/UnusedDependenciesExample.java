package cn.tuyucheng.taketoday.mavendependencyplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnusedDependenciesExample {
  
  public Logger getLogger() {
    return LoggerFactory.getLogger(UnusedDependenciesExample.class);
  }
}