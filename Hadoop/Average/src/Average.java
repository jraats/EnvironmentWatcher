import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Average {
	public static final String INPUT_DIR = "/hadoopData/Average/in/";
	public static final String HADOOP_INPUT_DIR = "/Average/in/";
	public static final String OUTPUT_DIR = "/hadoopData/Average/out/";
	public static final String HADOOP_OUTPUT_DIR = "/Average/out/";
	public static final boolean DEBUG = false;
	
	public static class AverageMap extends Mapper<LongWritable, Text, Text, Text> {

		@Override
		protected void map(LongWritable key, Text value, 
				Context context) throws IOException, InterruptedException {
			String filePathString = ((FileSplit) context.getInputSplit()).getPath().getName();
			
			context.write(new Text(filePathString), value);
			if(DEBUG) {
				System.out.println(filePathString);
				System.out.println("MAP: MAPVALUE " + value.toString());
			}
		};
	}
	
	public static class Combiner extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, 
				Context context) throws IOException, InterruptedException {
			if(DEBUG)
				System.out.println("COMBINER");
			Integer count = 0;
			Double sum = 0D;
			final Iterator<Text> itr = values.iterator();
			while (itr.hasNext()) {
				final String text = itr.next().toString();
				final Double value = Double.parseDouble(text);
				if(DEBUG)
					System.out.println("COMBINER VALUE: " + value);
				count++;
				sum += value;
			}

			final Double average = sum / count;

			context.write(new Text(key), new Text(average + "_" + count));
		};
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			if(DEBUG)
				System.out.println("REDUCER");
			Double sum = 0D;
			Integer totalcount = 0;
			final Iterator<Text> itr = values.iterator();
			while (itr.hasNext()) {
				final String text = itr.next().toString();
				final String[] tokens = text.split("_");
				final Double average = Double.parseDouble(tokens[0]);
				final Integer count = Integer.parseInt(tokens[1]);
				
				if(DEBUG) {
					System.out.println("REDUCER Average: " + average);
					System.out.println("REDUCER count: " + count);
				}
				
				sum += (average * count);
				totalcount += count;
			}

			final Double average = sum / totalcount;

			context.write(new Text(key), new Text(average.toString()));
		};
	}
	
	public static Job getJob(Configuration conf, FileSystem fs, ArrayList<String> files) throws Exception {
		Job job = Job.getInstance(conf, "average");
		job.setJarByClass(Average.class);
		job.setMapperClass(AverageMap.class);
		job.setCombinerClass(Combiner.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapOutputValueClass(Text.class);
		
		for(String file : files) {
			FileUtil.copy(new File(INPUT_DIR + file), fs, new Path(HADOOP_INPUT_DIR + file), true, conf);
			FileInputFormat.addInputPath(job, new Path(HADOOP_INPUT_DIR + file));
		}
		FileOutputFormat.setOutputPath(job, new Path(HADOOP_OUTPUT_DIR));
		
		return job;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		System.out.println("Start main loop!");
		while(true) {
			fs.delete(new Path(HADOOP_INPUT_DIR), true);
			fs.delete(new Path(HADOOP_OUTPUT_DIR), true);
			
			Thread.sleep(100);
			ArrayList<String> files = new ArrayList<String>();
			
			for (final File fileEntry : new File(INPUT_DIR).listFiles()) {
		        if (!fileEntry.isDirectory()) {
		            files.add(fileEntry.getName());
		        }
		    }
			
			if(files.size() == 0) {
				continue;
			}
			
			Job job = Average.getJob(conf, fs, files);
			
			if(!job.waitForCompletion(true)) {
				System.exit(1);
			}
			
			FileUtil.copy(fs, new Path(HADOOP_OUTPUT_DIR + "/part-r-00000"), new File(OUTPUT_DIR + "file"), false, conf);
			
			InputStream fis = new FileInputStream(OUTPUT_DIR + "file");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] splitvalue = line.split("\\t");
		    	String file = splitvalue[0];
		    	String value = splitvalue[1];
		    	
		    	Writer writer = new FileWriter(OUTPUT_DIR + file);
		    	writer.write(value);
		    	writer.close();
		    }
		    fis.close();
		}
	}
}
