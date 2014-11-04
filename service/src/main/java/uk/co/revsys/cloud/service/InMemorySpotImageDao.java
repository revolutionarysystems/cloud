package uk.co.revsys.cloud.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemorySpotImageDao implements SpotImageDao{

    private final List<SpotImage> images = new LinkedList<SpotImage>();
    private final Map<String, SpotImage> index = new HashMap<String, SpotImage>();

    public InMemorySpotImageDao(List<SpotImage> images) {
        for(SpotImage image: images){
            try {
                create(image);
            } catch (DaoException ex) {
                // Ignore
            }
        }
    }
    
    @Override
    public SpotImage create(SpotImage image) throws DaoException {
        images.add(image);
        index.put(image.getName(), image);
        return image;
    }

    @Override
    public SpotImage update(SpotImage image) throws DaoException {
        images.remove(index.get(image.getName()));
        return create(image);
    }

    @Override
    public SpotImage findByName(String name) throws DaoException {
        return index.get(name);
    }

    @Override
    public List<SpotImage> findAll() throws DaoException {
        return images;
    }

}
