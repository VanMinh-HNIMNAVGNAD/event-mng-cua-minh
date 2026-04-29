package com.sa.event_mng.shared.infrastructure.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sa.event_mng.modules.ordering.domain.model.Order;
import com.sa.event_mng.modules.ordering.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateOrderInvoice(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, java.awt.Color.BLACK);
            Paragraph header = new Paragraph("HOA DON DIEN TU / E-INVOICE", headerFont);
            header.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph(" "));

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            document.add(new Paragraph("Ma don hang / Order ID: #" + order.getId(), infoFont));
            document.add(new Paragraph("Khach hang / Customer: " + order.getCustomer().getFullName(), infoFont));
            document.add(new Paragraph("Ngay thanh toan / Date: " + (order.getPaidAt() != null ? order.getPaidAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : order.getOrderDate()), infoFont));
            document.add(new Paragraph("Phuong thuc / Payment: " + order.getPaymentMethod(), infoFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2.5f});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            String[] headers = {"Dich vu / Event & Ticket", "SL", "Gia / Price", "Thanh tien"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getTicketType().getEvent().getName() + "\n(" + item.getTicketType().getName() + ")", infoFont));
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), infoFont));
                qtyCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(qtyCell);
                
                table.addCell(new Phrase(String.format("%,.0f", item.getUnitPrice()) + " d", infoFont));
                table.addCell(new Phrase(String.format("%,.0f", item.getSubtotal()) + " d", infoFont));
            }
            document.add(table);

            Paragraph totals = new Paragraph();
            totals.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            java.math.BigDecimal subTotalVal = order.getTotalAmount().add(order.getDiscountAmount() != null ? order.getDiscountAmount() : java.math.BigDecimal.ZERO);
            totals.add(new Phrase("Tam tinh / Subtotal: " + String.format("%,.0f", subTotalVal) + " d\n", infoFont));
            
            if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                totals.add(new Phrase("Giam gia / Discount: -" + String.format("%,.0f", order.getDiscountAmount()) + " d (" + order.getVoucherCode() + ")\n", infoFont));
            }
            
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, java.awt.Color.RED);
            totals.add(new Phrase("\nTONG CONG / TOTAL PAID: " + String.format("%,.0f", order.getTotalAmount()) + " VND", totalFont));
            document.add(totals);

            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Cam on quy khach da tin tuong Event Hub!\nThank you for choosing Event Hub!", 
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, java.awt.Color.GRAY));
            footer.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
