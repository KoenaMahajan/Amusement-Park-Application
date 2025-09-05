import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatMessageDTO } from '../../../services/chat.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  messages: ChatMessageDTO[] = [];
  input = '';
  isSending = false;
  error: string | null = null;

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.chatService.getHistory().subscribe({
      next: (data) => { this.messages = data; },
      error: (err: Error) => { this.error = err.message; }
    });
  }

  send(): void {
    if (!this.input.trim()) return;
    const userText = this.input;
    this.isSending = true;
    this.error = null;
    this.chatService.sendMessage(userText).subscribe({
      next: (res) => {
        // Append to list optimistically
        this.messages = [...this.messages, { userMessage: userText, botReply: res.reply, createdAt: new Date().toISOString() }];
        this.input = '';
        this.isSending = false;
      },
      error: (err: Error) => { this.error = err.message; this.isSending = false; }
    });
  }
}


