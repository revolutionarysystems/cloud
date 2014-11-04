package uk.co.revsys.cloud.service;

import java.util.List;

public interface SpotImageDao {

    public SpotImage create(SpotImage image) throws DaoException;
    
    public SpotImage update(SpotImage image) throws DaoException;
    
    public SpotImage findByName(String name) throws DaoException;
    
    public List<SpotImage> findAll() throws DaoException;
    
}
