package Reika.ReactorCraft.Auxiliary;

import Reika.DragonAPI.Instantiable.Data.Maps.TileEntityCache;
import Reika.ReactorCraft.TileEntities.HTGR.TileEntityPebbleBed;

public class PebbleBedArrangement {

	private final TileEntityCache<TileEntityPebbleBed> positions = new TileEntityCache();

	public PebbleBedArrangement(TileEntityPebbleBed te) {
		positions.put(te);
	}

	/** The one being fed in is the one being 'eaten' */
	public void merge(PebbleBedArrangement pba) {
		if (pba == this)
			return;
		if (pba == null || pba.positions.isEmpty())
			return;
		positions.putAll(pba.positions);
		for (TileEntityPebbleBed te : pba.positions.values()) {
			te.setReactorObject(this);
		}
		pba.clear();
	}

	public void clear() {
		positions.clear();
	}

	public int getSize() {
		return positions.size();
	}

	public void add(TileEntityPebbleBed te) {
		positions.put(te);
	}

	public void remove(TileEntityPebbleBed te) {
		positions.remove(te);
	}

	@Override
	public String toString() {
		return "#"+this.hashCode()+" > "+this.getSize()+": "+positions.keySet().toString();
	}

}
