import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Average {

	public static class AverageMap extends Mapper<LongWritable, Text, Text, Text> {

		@Override
		protected void map(LongWritable key, Text value, 
				Context context) throws IOException, InterruptedException {
			context.write(new Text("MAPPER"), value);
			System.out.println("MAP: MAPVALUE " + value.toString());
		};
	}
	
	public static class Combiner extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, 
				Context context) throws IOException, InterruptedException {
			System.out.println("COMBINER");
			Integer count = 0;
			Double sum = 0D;
			final Iterator<Text> itr = values.iterator();
			while (itr.hasNext()) {
				final String text = itr.next().toString();
				final Double value = Double.parseDouble(text);
				System.out.println("COMBINER VALUE: " + value);
				count++;
				sum += value;
			}

			final Double average = sum / count;

			context.write(new Text("A_C"), new Text(average + "_" + count));
		};
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			System.out.println("REDUCER");
			Double sum = 0D;
			Integer totalcount = 0;
			final Iterator<Text> itr = values.iterator();
			while (itr.hasNext()) {
				final String text = itr.next().toString();
				final String[] tokens = text.split("_");
				final Double average = Double.parseDouble(tokens[0]);
				final Integer count = Integer.parseInt(tokens[1]);
				
				System.out.println("REDUCER Average: " + average);
				System.out.println("REDUCER count: " + count);
				
				sum += (average * count);
				totalcount += count;
			}

			final Double average = sum / totalcount;

			context.write(new Text("AVERAGE"), new Text(average.toString()));
		};
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
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
		
		FileSystem fs = FileSystem.get(conf);
		
		FileUtil.copy(new File("/hadoopData/Average/in/" + args[0]), fs, new Path("/Average/in/file"), true, conf);
		
		FileInputFormat.addInputPath(job, new Path("/Average/in"));
		FileOutputFormat.setOutputPath(job, new Path("/Average/out"));
		
		if(!job.waitForCompletion(true)) {
			System.exit(1);
		}
		
		FileUtil.copy(fs, new Path("/Average/out/part-r-00000"), new File("/hadoopData/Average/out/" + args[1]), false, conf);
		fs.delete(new Path("/Average"), true);
		System.exit(0);
	}
}
