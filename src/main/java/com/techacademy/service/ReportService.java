package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // 検索してリストを取得
        List<Report> reportList = findByEmployee(report.getEmployee());

        // リストを元に日付をチェックする
        // リストが空じゃなかったら
        if (reportList != null) {
            // リストを1つずつ
            for (Report checkReport:reportList) {
                // リストのうちの1つの日報の日付と今回入力した日付がイコール
                // equals…記号の"=="だと厳密でないので、同じ型同士のものの完全一致を見る
                if (checkReport.getReportDate().equals(report.getReportDate())) {
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
        }

        report.setDeleteFlg(false);

        // 新規登録の場合
        if (report.getCreatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            report.setCreatedAt(now);
            report.setUpdatedAt(now);
        // 更新の場合
        } else {
            Report reportOld = findById(report.getId());
            report.setCreatedAt(reportOld.getCreatedAt());
            LocalDateTime now = LocalDateTime.now();
            report.setUpdatedAt(now);
        }

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // ログイン社員番号の日報を検索
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }


}