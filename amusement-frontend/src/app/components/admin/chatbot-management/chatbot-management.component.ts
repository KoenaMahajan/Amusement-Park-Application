import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotKeyword, ChatbotService } from '../../../services/chatbot.service';

@Component({
  selector: 'app-chatbot-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot-management.component.html',
  styleUrls: ['./chatbot-management.component.css']
})
export class ChatbotManagementComponent implements OnInit {
  keywords: ChatbotKeyword[] = [];
  newKeyword: ChatbotKeyword = { keyword: '', response: '' };
  isLoading = false;
  message: string | null = null;

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;
    this.chatbotService.getAll().subscribe({
      next: (data) => { this.keywords = data; this.isLoading = false; },
      error: (err: Error) => { this.message = err.message; this.isLoading = false; }
    });
  }

  addKeyword(): void {
    if (!this.newKeyword.keyword || !this.newKeyword.response) return;
    this.chatbotService.add(this.newKeyword).subscribe({
      next: () => { this.newKeyword = { keyword: '', response: '' }; this.loadAll(); },
      error: (err: Error) => { this.message = err.message; }
    });
  }

  updateKeyword(k: ChatbotKeyword): void {
    if (!k.id) return;
    this.chatbotService.update(k.id, { keyword: k.keyword, response: k.response }).subscribe({
      next: () => { this.loadAll(); },
      error: (err: Error) => { this.message = err.message; }
    });
  }

  deleteKeyword(id?: number): void {
    if (!id) return;
    this.chatbotService.delete(id).subscribe({
      next: () => { this.loadAll(); },
      error: (err: Error) => { this.message = err.message; }
    });
  }
}


