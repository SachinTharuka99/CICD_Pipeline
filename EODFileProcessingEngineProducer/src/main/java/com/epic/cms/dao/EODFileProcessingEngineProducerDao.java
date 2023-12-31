/**
 * Author :
 * Date : 4/6/2023
 * Time : 7:42 AM
 * Project Name : ecms_eod_product
 */

package com.epic.cms.dao;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface EODFileProcessingEngineProducerDao {
    int getCurrentEODId(String status) throws Exception;

    String getFileStatus(String query, String fileId) throws Exception;

    void updateFileStatus(String query, String fileId, String status) throws Exception;

    HashMap<String, List<String>> getAllProcessingPendingFiles() throws Exception;

    String getProcessIdByUniqueId(String uniqueId) throws Exception;

    List<String> getErrorProcessIdList() throws Exception;

    void updateProcessProgressForErrorProcess(String processId) throws Exception;

    void updateEodProcessStateCount() throws Exception;

    void updateEodProcessProgress(int successCount, int failedCount, String progress, int processId) throws Exception;
}

