package com.sa.event_mng.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sa.event_mng.model.entity.Order;
import com.sa.event_mng.model.entity.OrderItem;
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

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("E-INVOICE / HOA DON DIEN TU", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Order ID: " + order.getId(), infoFont));
            document.add(new Paragraph("Customer: " + order.getCustomer().getFullName(), infoFont));
            document.add(new Paragraph("Date: " + order.getOrderDate(), infoFont));
            document.add(new Paragraph("Payment Method: " + order.getPaymentMethod(), infoFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 2, 2, 2});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("Event / Ticket Type", headFont));
            hcell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Quantity", headFont));
            hcell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Unit Price", headFont));
            hcell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Subtotal", headFont));
            hcell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(hcell);

            for (OrderItem item : order.getItems()) {
                PdfPCell cell;

                String itemName = item.getTicketType().getEvent().getName() + " - " + item.getTicketType().getName();
                cell = new PdfPCell(new Phrase(itemName));
                cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity())));
                cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(item.getUnitPrice())));
                cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(item.getSubtotal())));
                cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                table.addCell(cell);
            }

            document.add(table);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("SubTotal: " + order.getTotalAmount().add(order.getDiscountAmount() != null ? order.getDiscountAmount() : java.math.BigDecimal.ZERO), infoFont));
            if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("Discount: -" + order.getDiscountAmount() + " (Voucher: " + order.getVoucherCode() + ")", infoFont));
            }
            
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Total Paid: " + order.getTotalAmount() + " VND", totalFont));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
