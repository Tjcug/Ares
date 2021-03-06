/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.basic.benchmark.bolt;

import com.basic.benchmark.util.FileUtil;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


public class PrinterBolt extends BaseBasicBolt {
  private int taskid;
  private BufferedOutputStream genderbufferedOutputStream;
  private BufferedOutputStream agebufferedOutputStream;

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    this.taskid=context.getThisTaskId();
    String genderfileName="/root/TJ/aresbench/genderlatency-"+taskid;
    String agefileName="/root/TJ/aresbench/agelatency-"+taskid;
    try {
      File genderfile = new File(genderfileName);
      File agefile = new File(agefileName);
      FileUtil.createFile(genderfile);
      FileUtil.createFile(agefile);
      genderbufferedOutputStream= new BufferedOutputStream(new FileOutputStream(genderfile,true)) ;
      agebufferedOutputStream= new BufferedOutputStream(new FileOutputStream(agefile,true)) ;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {
    Long genderstartTime = tuple.getLongByField("gendertimeinfo");
    Long agestartTime = tuple.getLongByField("agetimeinfo");
    Long endTime=System.currentTimeMillis();
    String genderdelayTime=(endTime-genderstartTime)+"\t"+endTime+"\n";
    String agedelayTime=(endTime-agestartTime)+"\t"+endTime+"\n";
    try {
      genderbufferedOutputStream.write(genderdelayTime.getBytes("UTF-8"));
      agebufferedOutputStream.write(agedelayTime.getBytes("UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer ofd) {
  }

  @Override
  public void cleanup() {
    try {
      genderbufferedOutputStream.close();
      agebufferedOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
