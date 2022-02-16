package com.lin;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class FlinkCDC {
    public static void main(String[] args) throws Exception {
        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //1.1 开启CK并指定状态后端为FS    memory  fs  rocksdb
        env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/gmall-flink-210325/ck"));
        env.enableCheckpointing(5000L);//每次checkpoint头与头的间隔时间
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);//每次checkpoint的持续时间
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);//同时存在的checkpoint
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(3000);//头与尾的间隔时间

        //2.通过FlinkCDC构建SourceFunction并读取数据
        DebeziumSourceFunction<String> sourceFunction = MySQLSource.<String>builder()
                .hostname("xpx101")
                .port(3306)
                .username("root")
                .password("123456")
                .databaseList("gmall-flink")
                .tableList("gmall-flink.base_trademark")
                .deserializer(new StringDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();


        DataStreamSource<String> streamSource = env.addSource(sourceFunction);

        //3.打印数据
        streamSource.print();

        //4.启动任务
        env.execute("FlinkCDC");

    }
}
