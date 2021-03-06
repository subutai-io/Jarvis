package org.safehaus.service.impl;


import java.util.List;

import org.safehaus.dao.CaptureDao;
import org.safehaus.model.Capture;
import org.safehaus.model.CaptureNotFoundException;
import org.safehaus.service.api.CaptureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service( "captureManager" )
@Transactional
public class CaptureManagerImpl extends GenericManagerImpl<Capture, Long> implements CaptureManager
{
    private static Logger logger = LoggerFactory.getLogger( CaptureManagerImpl.class );

    private CaptureDao captureDao;


    @Autowired
    public void setCaptureDao( final CaptureDao captureDao )
    {
        this.captureDao = captureDao;
    }


    @Override
    public List<Capture> getCapturesByUsername( final String username )
    {
        return captureDao.getCapturesByUsername( username );
    }


    @Override
    public List<Capture> getAll()
    {
        return captureDao.getAll();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Capture getCapture( final String captureId ) throws CaptureNotFoundException
    {
        return captureDao.getCapture( captureId );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Capture saveCapture( final Capture capture )
    {
        return captureDao.saveCapture( capture );
    }


    @Override
    public void removeCapture( final Capture capture )
    {
        captureDao.remove( capture );
    }
}
