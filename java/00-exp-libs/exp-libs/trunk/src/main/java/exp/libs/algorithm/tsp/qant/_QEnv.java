package exp.libs.algorithm.tsp.qant;

final class _QEnv {

	/** 默认最大的蚂蚁代数（迭代次数） */
	private final static int DEFAULT_MAX_GENERATION = 10;
	
	/** 量子蚂蚁最大代数 */
	private int maxGeneration;
	
	/** 变异处理阀值: 当连续N次求解但没有更新最优解时, 则执行量子交叉, 避免搜索陷入停滞 */
	private int qCrossThreshold;
	
	/** 使用变异处理，可避免算法停滞到局部解或无解，但降低收敛速度 */
	public boolean useQCross;
	
	/** 使用信息素自然挥发，可加速收敛到局部解或无解，但降低收敛速度 */
	public boolean useVolatilize;
	
	/** 拓扑图的量子参数 */
	private __QGraph qGraph;
	
	/**
	 * 构造函数
	 * @param dist 拓扑图节点间距
	 * @param srcId 拓扑图源点编号
	 * @param snkId 拓扑图终点编号
	 * @param maxGeneration
	 * @param useQCross
	 * @param useVolatilize
	 */
	protected _QEnv(int[][] dist, int srcId, int snkId, 
			int maxGeneration, boolean useQCross, boolean useVolatilize) {
		this.qGraph = new __QGraph(dist, srcId, snkId);
		this.maxGeneration = (maxGeneration <= 0 ? 
				DEFAULT_MAX_GENERATION : maxGeneration);
		this.qCrossThreshold = maxGeneration / 2;
		this.useQCross = useQCross;
		this.useVolatilize = useVolatilize;
	}
	
	protected int size() {
		return qGraph.size();
	}

	protected int srcId() {
		return qGraph.srcId();
	}

	protected int snkId() {
		return qGraph.snkId();
	}

	protected int dist(int srcId, int snkId) {
		return qGraph.dist(srcId, snkId);
	}

	protected double eta(int srcId, int snkId) {
		return qGraph.eta(srcId, snkId);
	}

	protected double avgDist(int nodeId) {
		return qGraph.avgDist(nodeId);
	}

	protected int maxDist(int nodeId) {
		return qGraph.maxDist(nodeId);
	}

	protected int MAX_GENERATION() {
		return maxGeneration;
	}
	
	protected int QCROSS_THRESHOLD() {
		return qCrossThreshold;
	}

	protected boolean isUseQCross() {
		return useQCross;
	}

	protected boolean isUseVolatilize() {
		return useVolatilize;
	}
	
}