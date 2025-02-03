package com.ibm.coc.qotd.qrcode;

public class ManagedMetrics {
	
	public class MetricApi {
		public int mean;
		public int stdev;
		public int min;
		public int max;
		
		public int getMean() {
			return mean;
		}
		public void setMean(int mean) {
			this.mean = mean;
		}
		public int getStdev() {
			return stdev;
		}
		public void setStdev(int stdev) {
			this.stdev = stdev;
		}
		public int getMin() {
			return min;
		}
		public void setMin(int min) {
			this.min = min;
		}
		public int getMax() {
			return max;
		}
		public void setMax(int max) {
			this.max = max;
		}
	}
	
	public int Cpu = 0;
	public int Mem = 0;
	public MetricApi Api = new MetricApi();
	public int getCpu() {
		return Cpu;
	}
	public void setCpu(int cpu) {
		Cpu = cpu;
	}
	public int getMem() {
		return Mem;
	}
	public void setMem(int mem) {
		Mem = mem;
	}
	public MetricApi getApi() {
		return Api;
	}
	public void setApi(MetricApi api) {
		Api = api;
	}
	
	
}
