package decc.options;

/**
 * Options builder
 * @author nyradr
 */
public class OptionsBuilder {
	
	/**
	 * Implementation of options
	 * @author nyradr
	 */
	private static class OptionImpl extends Options{
		/**
		 * Create options class filled with default values
		 */
		private OptionImpl(){
			maxpeer = 50;
			maxRoads = 200;
			allowBS = false;
		}
		
		/**
		 * Create copy of options
		 * @param cpy
		 */
		private OptionImpl(OptionImpl cpy){
			maxpeer = cpy.maxpeer;
			maxRoads = cpy.maxRoads;
		}
	}
	
	private OptionImpl opt;
	
	/**
	 * Create Options builder<br>
	 * All values are set to default
	 */
	public OptionsBuilder(){
		opt = new OptionImpl();
	}
	
	/**
	 * Set the maximum number of connected peers
	 * @param max max should be greater than 0
	 * @return true if success
	 */
	public boolean setMaxPeers(int max){
		if(max > 0)
			opt.maxpeer = max;
		
		return max > 0;
	}
	
	public int getMaxPeers(){
		return opt.maxpeer;
	}
	
	/**
	 * Set the maximum number of roads passing through this peers (including communications)
	 * @param max max should be greater than 0
	 * @return true if success
	 */
	public boolean setMaxRoads(int max){
		if(max > 0)
			opt.maxRoads = max;
		
		return max > 0;
	}
	
	public int getMaxRoads(){
		return opt.maxRoads;
	}
	
	/**
	 * Build options class
	 * @return
	 */
	public Options build(){
		return new OptionImpl(opt);
	}
	
	/**
	 * Get options class filled with default values
	 * @return
	 */
	public static Options getDefault(){
		return new OptionImpl();
	}
}
