package com.sa.event_mng.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sa.event_mng.model.entity.Order;
import com.sa.event_mng.model.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class InvoiceService {

    public byte[] generateInvoicePdf(Order order) throws IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font setup
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("ELECTRONIC INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Order Info
            document.add(new Paragraph("Order ID: " + order.getId(), headerFont));
            document.add(new Paragraph("Customer: " + order.getCustomer().getFullName(), normalFont));
            document.add(new Paragraph("Date: " + order.getOrderDate().toString(), normalFont));
            document.add(new Paragraph("Payment Method: " + order.getPaymentMethod(), normalFont));
            document.add(new Paragraph(" ", normalFont));

            // Table for Items
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[] {4, 2, 2, 2});

            // Table Headers
            table.addCell(new PdfPCell(new Phrase("Event / Ticket Type", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Price", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Qty", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Subtotal", headerFont)));

            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getTicketType().getEvent().getName() + " - " + item.getTicketType().getName(), normalFont));
                table.addCell(new Phrase(item.getUnitPrice().toString() + " VND", normalFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                table.addCell(new Phrase(item.getSubtotal().toString() + " VND", normalFont));
            }
            document.add(table);

            // Summary
            document.add(new Paragraph(" ", normalFont));
            Paragraph summary = new Paragraph();
            summary.setAlignment(Element.ALIGN_RIGHT);
            if (order.getDiscountAmount() != null && order.getDiscountAmount().doubleValue() > 0) {
                summary.add(new Phrase("Discount: -" + order.getDiscountAmount() + " VND\n", normalFont));
            }
            summary.add(new Phrase("TOTAL AMOUNT: " + order.getTotalAmount() + " VND", titleFont));
            document.add(summary);

            // Footer
            Paragraph footer = new Paragraph("\n\nThank you for choosing Event Manager!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
